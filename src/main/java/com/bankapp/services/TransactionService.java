package com.bankapp.services;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.constants.Constants;
import com.bankapp.encryption.RSACipher;
import com.bankapp.exceptions.TimeExpiredException;
import com.bankapp.models.Account;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.repositories.AccountRepository;
import com.bankapp.repositories.TransactionRepository;

import apple.laf.JRSUIConstants.State;
import scala.annotation.meta.setter;

@Service
public class TransactionService implements ITransactionService, Constants {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IUserService userService;

    private long MAX_VALID_TIME = 1;

    @Transactional
    @Override
    public List<Transaction> getTransactionsByAccount(Account fromAccount, Account toAccount) {
        List<Transaction> list = transactionRepository.findByFromAccountOrToAccountOrderByCreatedDesc(fromAccount,
                toAccount);
        return list;
    }

    @Transactional
    @Override
    public String saveTransaction(String fromEmail, String toEmail, Transaction transaction) {

        User fromUser = userService.getUserByEmail(fromEmail);
        User toUser = userService.getUserByEmail(toEmail);

        if (fromUser == null || toUser == null) {
            return ERR_ACCOUNT_NOT_EXISTS;
        }

        if (fromUser.equals(toUser)) {
            return ERR_SAME_USER;
        }

        byte[] privateKeyBytes = fromUser.getPublicKey();
        try {
            String amount = decryptAmount(privateKeyBytes, transaction.getEncryptedAmount());
            Double parsedAmount = Double.parseDouble(amount);
            transaction.setAmount(parsedAmount);
            transaction.setEncryptedAmount(null);
        } catch (UnsupportedEncodingException e) {
            return ERR_TRANS_DECODE;
        } catch (GeneralSecurityException e) {
            return ERR_TRANS_DECRYPTION;
        } catch (NumberFormatException e) {
            return ERR_TRANS_INCORRECT_FORMAT;
        } catch (TimeExpiredException e) {
            return ERR_TRANS_EXPIRED;
        } catch (Exception e) {
            return ERR_UNHANDLED;
        }

        Account fromAccount = accountService.getAccountByUser(fromUser);
        Account toAccount = accountService.getAccountByUser(toUser);
        if (fromAccount == null || toAccount == null) {
            return ERR_ACCOUNT_NOT_EXISTS;
        }
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setTransferDate(new Date());

        if (fromAccount.getBalance() < transaction.getAmount()) {
            transaction.setStatus(S_DECLINED);
            transactionRepository.save(transaction);
            return ERR_LESS_BALANCE;
        } else {
            boolean ifCritical = isBelowCriticalLimit(fromAccount, transaction);
            if (ifCritical) {
                transaction.setStatus(S_OTP_PENDING);
                transactionRepository.save(transaction);
                return CRITICAL;
            }
            String message = accountService.updateBalance(transaction);
            transaction.setStatus(S_VERIFIED);
            transactionRepository.save(transaction);
            return message;
        }
    }

    private boolean isBelowCriticalLimit(Account fromAccount, Transaction transaction) {
        double criticalLimit = fromAccount.getCriticalLimit();
        if (transaction.getAmount() >= criticalLimit) {
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public String askCustomerPayment(Transaction transaction, User user) {
        try {
            if (transaction.getFromAccount() == null) {
                return ERR_ACCOUNT_NOT_EXISTS;
            }
            transaction.setToAccount(accountService.getAccountByUser(user));
            transaction.setStatus(S_PENDING_CUSTOMER_VERIFICATION);
            Date date = new Date();
            transaction.setTransferDate(date);
            transactionRepository.save(transaction);
            return SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        }
    }

    @Transactional
    @Override
    public String initiateTransaction(String fromEmail, String toEmail, Transaction transaction) {
        try {

            User fromUser = userService.getUserByEmail(fromEmail);
            User toUser = userService.getUserByEmail(toEmail);

            if (fromUser == null || toUser == null) {
                return ERR_ACCOUNT_NOT_EXISTS;
            }

            if (fromUser.equals(toUser)) {
                return ERR_SAME_USER;
            }

            byte[] privateKeyBytes = fromUser.getPublicKey();
            try {
                String amount = decryptAmount(privateKeyBytes, transaction.getEncryptedAmount());
                Double parsedAmount = Double.parseDouble(amount);
                transaction.setAmount(parsedAmount);
                transaction.setEncryptedAmount(null);
            } catch (UnsupportedEncodingException e) {
                return ERR_TRANS_DECODE;
            } catch (GeneralSecurityException e) {
                return ERR_TRANS_DECRYPTION;
            } catch (NumberFormatException e) {
                return ERR_TRANS_INCORRECT_FORMAT;
            } catch (TimeExpiredException e) {
                return ERR_TRANS_EXPIRED;
            } catch (Exception e) {
                return ERR_UNHANDLED;
            }

            Account fromAccount = accountService.getAccountByUser(fromUser);
            Account toAccount = accountService.getAccountByUser(toUser);
            if (fromAccount == null || toAccount == null) {
                return ERR_ACCOUNT_NOT_EXISTS;
            }
            transaction.setFromAccount(fromAccount);
            transaction.setToAccount(toAccount);
            transaction.setStatus(S_PENDING);
            transactionRepository.save(transaction);
            return SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        }
    }

    @Transactional
    @Override
    public String creditDebit(String Email, Transaction transaction) {
        try {

            User fromUser = userService.getUserByEmail(Email);
            User toUser = fromUser;
            
            if (fromUser == null || toUser == null) {
                return ERR_ACCOUNT_NOT_EXISTS;
            }
            
            byte[] privateKeyBytes = fromUser.getPublicKey();
            try {
                String amount = decryptAmount(privateKeyBytes, transaction.getEncryptedAmount());
                Double parsedAmount = Double.parseDouble(amount);
                transaction.setAmount(parsedAmount);
                transaction.setEncryptedAmount(null);
            } catch (UnsupportedEncodingException e) {
                return ERR_TRANS_DECODE;
            } catch (GeneralSecurityException e) {
                return ERR_TRANS_DECRYPTION;
            } catch (NumberFormatException e) {
                return ERR_TRANS_INCORRECT_FORMAT;
            } catch (TimeExpiredException e) {
                return ERR_TRANS_EXPIRED;
            } catch (Exception e) {
                return ERR_UNHANDLED;
            }

            Account fromAccount = accountService.getAccountByUser(fromUser);
            Account toAccount = fromAccount;

            String status = transaction.getStatus();
            if (status.equalsIgnoreCase("A_Credit")) {
                transaction.setStatus(A_CREDIT);
            } else {
                transaction.setStatus(A_DEBIT);
                
                if(fromAccount.getBalance() < transaction.getAmount()) {
                    return ERR_LESS_BALANCE;
                }
            }

            transaction.setFromAccount(fromAccount);
            transaction.setToAccount(toAccount);
            transactionRepository.save(transaction);
            return SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        }
    }

    @Override
    public Transaction getTransactionsById(String id) {
        return transactionRepository.findOne(id);
    }

    @Override
    public List<Transaction> getMerchantRequests(String status) {
        List<Transaction> transactions = transactionRepository.findByStatus(status);
        return transactions;
    }

    @Transactional
    @Override
    public String actionOnRequest(String id, String status) {
        try {
            Transaction transaction = transactionRepository.findOne(id);
            transaction.setStatus(status);
            if (status.equals(S_CUSTOMER_VERIFIED)) {
                String msg = saveTransaction(transaction.getFromAccount().getUser().getEmail(),
                        transaction.getToAccount().getUser().getEmail(), transaction);
                return msg;
            }
            transactionRepository.save(transaction);
            return SUCCESS;
        } catch (Exception e) {
            return ERROR;
        }
    }

    @Transactional
    @Override
    public List<Transaction> getPendingTransactions() {
        return transactionRepository.findByStatus(S_PENDING);
    }

    private String decryptAmount(byte[] key, String cipherText)
            throws UnsupportedEncodingException, GeneralSecurityException, TimeExpiredException {

        RSACipher cipher = new RSACipher();
        String decrypted = cipher.decrypt(cipherText, key);
        String[] decryptedWords = decrypted.split("/");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime expiration = LocalDateTime.parse(decryptedWords[1], formatter);
        LocalDateTime now = LocalDateTime.now();
        long dur = Duration.between(expiration, now).toMinutes();

        if (dur > MAX_VALID_TIME) {
            throw new TimeExpiredException();
        }

        return decryptedWords[0];
    }

    @Transactional
    @Override
    public List<Transaction> getCreditDebitRequest() {
        List<Transaction> list = transactionRepository.findByStatus(A_CREDIT);
        list.addAll(transactionRepository.findByStatus(A_DEBIT));
        return list;
    }

    @Override
    public String creditDebitTransaction(User user, String action, Transaction transaction) {
        try{
            byte[] privateKeyBytes = user.getPublicKey();
            try {
                String amount = decryptAmount(privateKeyBytes, transaction.getEncryptedAmount());
                Double parsedAmount = Double.parseDouble(amount);
                transaction.setAmount(parsedAmount);
                transaction.setEncryptedAmount(null);
            } catch (UnsupportedEncodingException e) {
                return ERR_TRANS_DECODE;
            } catch (GeneralSecurityException e) {
                return ERR_TRANS_DECRYPTION;
            } catch (NumberFormatException e) {
                return ERR_TRANS_INCORRECT_FORMAT;
            } catch (TimeExpiredException e) {
                return ERR_TRANS_EXPIRED;
            } catch (Exception e) {
                return ERR_UNHANDLED;
            }
            double amount = 0;
            if(action.equals(A_CREDIT)){
                 amount = transaction.getToAccount().getBalance() + transaction.getAmount();
                
            } else{
                if (transaction.getToAccount().getBalance() < transaction.getAmount()) {
                    transaction.setStatus(S_DECLINED);
                    transactionRepository.save(transaction);
                    return ERR_LESS_BALANCE;
                }
                amount = transaction.getToAccount().getBalance() - transaction.getAmount();
            }
            transaction.getToAccount().setBalance(amount);
            transaction.setStatus(S_VERIFIED);
            accountService.saveAccount(transaction.getToAccount());
            transactionRepository.save(transaction);
        }catch(Exception e){
            return ERROR;
        }
        return SUCCESS;
    }
}