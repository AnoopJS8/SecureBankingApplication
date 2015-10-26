package com.bankapp.services;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.constants.Constants;
import com.bankapp.encryption.RSACipher;
import com.bankapp.exceptions.TimeExpiredException;
import com.bankapp.models.Account;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.repositories.TransactionRepository;

@Service
public class TransactionService implements ITransactionService, Constants {

    private final Logger logger = Logger.getLogger(MailService.class);

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
        List<Transaction> transactions = transactionRepository
                .findByFromAccountOrToAccountOrderByCreatedDesc(fromAccount, toAccount);

        String logMessageFormat = "[Action=%s][FromAccount=%s, ToAccount=%s]";
        String logMessage = String.format(logMessageFormat, "getTransactionsByAccount", fromAccount.getAccId(),
                toAccount.getAccId());
        logger.info(logMessage);

        return transactions;
    }

    @Transactional
    @Override
    public String saveTransaction(String fromEmail, String toEmail, Transaction transaction) {

        User fromUser = userService.getUserByEmail(fromEmail);
        User toUser = userService.getUserByEmail(toEmail);

        if (fromUser == null || toUser == null) {
            String logMessageFormat = "[Action=%s][Status=%s][FromEmail=%s, ToEmail=%s]";
            String logMessage = String.format(logMessageFormat, "saveTransaction", ERR_ACCOUNT_NOT_EXISTS, fromEmail,
                    toEmail);
            logger.info(logMessage);

            return ERR_ACCOUNT_NOT_EXISTS;
        }

        if (fromUser.equals(toUser)) {
            String logMessageFormat = "[Action=%s][Status=%s][FromEmail=%s]";
            String logMessage = String.format(logMessageFormat, "saveTransaction", ERR_SAME_USER, fromEmail);
            logger.info(logMessage);

            return ERR_SAME_USER;
        }

        byte[] privateKeyBytes = fromUser.getPublicKey();
        try {
            String amount = decryptAmount(privateKeyBytes, transaction.getEncryptedAmount());
            Double parsedAmount = Double.parseDouble(amount);
            transaction.setAmount(parsedAmount);
            transaction.setEncryptedAmount(null);
        } catch (UnsupportedEncodingException e) {
            String logMessageFormat = "[Action=%s][Status=%s][FromEmail=%s, ToEmail=%s]";
            String logMessage = String.format(logMessageFormat, "saveTransaction", ERR_TRANS_DECODE, fromEmail,
                    toEmail);
            logger.info(logMessage);

            return ERR_TRANS_DECODE;
        } catch (GeneralSecurityException e) {
            String logMessageFormat = "[Action=%s][Status=%s][FromEmail=%s, ToEmail=%s]";
            String logMessage = String.format(logMessageFormat, "saveTransaction", ERR_TRANS_DECRYPTION, fromEmail,
                    toEmail);
            logger.info(logMessage);

            return ERR_TRANS_DECRYPTION;
        } catch (NumberFormatException e) {
            String logMessageFormat = "[Action=%s][Status=%s][FromEmail=%s, ToEmail=%s]";
            String logMessage = String.format(logMessageFormat, "saveTransaction", ERR_TRANS_INCORRECT_FORMAT,
                    fromEmail, toEmail);
            logger.info(logMessage);

            return ERR_TRANS_INCORRECT_FORMAT;
        } catch (TimeExpiredException e) {
            String logMessageFormat = "[Action=%s][Status=%s][FromEmail=%s, ToEmail=%s]";
            String logMessage = String.format(logMessageFormat, "saveTransaction", ERR_TRANS_EXPIRED, fromEmail,
                    toEmail);
            logger.info(logMessage);

            return ERR_TRANS_EXPIRED;
        } catch (Exception e) {
            String logMessageFormat = "[Action=%s][Status=%s][FromEmail=%s, ToEmail=%s]";
            String logMessage = String.format(logMessageFormat, "saveTransaction", ERR_UNHANDLED, fromEmail, toEmail);
            logger.info(logMessage);

            return ERR_UNHANDLED;
        }

        Account fromAccount = accountService.getAccountByUser(fromUser);
        Account toAccount = accountService.getAccountByUser(toUser);

        if (fromAccount == null || toAccount == null) {
            String logMessageFormat = "[Action=%s][Status=%s][FromEmail=%s, ToEmail=%s]";
            String logMessage = String.format(logMessageFormat, "saveTransaction", ERR_ACCOUNT_NOT_EXISTS, fromEmail,
                    toEmail);
            logger.info(logMessage);

            return ERR_ACCOUNT_NOT_EXISTS;
        }

        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setTransferDate(new Date());

        if (fromAccount.getBalance() < transaction.getAmount()) {
            transaction.setStatus(S_DECLINED);
            transactionRepository.save(transaction);

            String logMessageFormat = "[Action=%s][Status=%s][FromEmail=%s, ToEmail=%s, Transaction=%s]";
            String logMessage = String.format(logMessageFormat, "saveTransaction", ERR_LESS_BALANCE, fromEmail, toEmail,
                    transaction.getTransactionId());
            logger.info(logMessage);

            return ERR_LESS_BALANCE;
        } else {
            boolean ifCritical = isBelowCriticalLimit(fromAccount, transaction);

            if (ifCritical) {
                transaction.setStatus(S_OTP_PENDING);
                transactionRepository.save(transaction);

                String logMessageFormat = "[Action=%s][Status=%s][FromEmail=%s, ToEmail=%s, Transaction=%s]";
                String logMessage = String.format(logMessageFormat, "saveTransaction", CRITICAL, fromEmail, toEmail,
                        transaction.getTransactionId());
                logger.info(logMessage);

                return CRITICAL;
            }

            String message = accountService.updateBalance(transaction);
            transaction.setStatus(S_VERIFIED);
            transactionRepository.save(transaction);

            String logMessageFormat = "[Action=%s][Status=%s][FromEmail=%s, ToEmail=%s, Transaction=%s]";
            String logMessage = String.format(logMessageFormat, "saveTransaction", message, fromEmail, toEmail,
                    transaction.getTransactionId());
            logger.info(logMessage);

            return message;
        }
    }

    @Transactional
    @Override
    public String askCustomerPayment(Transaction transaction, User user) {
        try {
            if (transaction.getFromAccount() == null) {
                String logMessageFormat = "[Action=%s][Status=%s][User=%s, Transaction=%s]";
                String logMessage = String.format(logMessageFormat, "askCustomerPayment", ERR_ACCOUNT_NOT_EXISTS,
                        user.getEmail(), transaction.getTransactionId());
                logger.info(logMessage);

                return ERR_ACCOUNT_NOT_EXISTS;
            }
            transaction.setToAccount(accountService.getAccountByUser(user));
            transaction.setStatus(S_PENDING_CUSTOMER_VERIFICATION);
            Date date = new Date();
            transaction.setTransferDate(date);
            transactionRepository.save(transaction);

            String logMessageFormat = "[Action=%s][Status=%s][User=%s, Transaction=%s]";
            String logMessage = String.format(logMessageFormat, "askCustomerPayment", SUCCESS, user.getEmail(),
                    transaction.getTransactionId());
            logger.info(logMessage);

            return SUCCESS;
        } catch (Exception e) {
            String logMessageFormat = "[Action=%s][Status=%s][User=%s, Transaction=%s, Message=%s]";
            String logMessage = String.format(logMessageFormat, "askCustomerPayment", ERROR, user.getEmail(),
                    transaction.getTransactionId(), e.getMessage());
            logger.error(logMessage);

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
                String logMessageFormat = "[Action=%s][Status=%s][FromEmail=%s, ToEmail=%s]";
                String logMessage = String.format(logMessageFormat, "initiateTransaction", ERR_ACCOUNT_NOT_EXISTS,
                        fromEmail, toEmail);
                logger.info(logMessage);

                return ERR_ACCOUNT_NOT_EXISTS;
            }

            if (fromUser.equals(toUser)) {
                String logMessageFormat = "[Action=%s][Status=%s][FromEmail=%s]";
                String logMessage = String.format(logMessageFormat, "initiateTransaction", ERR_SAME_USER, fromEmail);
                logger.info(logMessage);

                return ERR_SAME_USER;
            }

            byte[] privateKeyBytes = fromUser.getPublicKey();
            try {
                String amount = decryptAmount(privateKeyBytes, transaction.getEncryptedAmount());
                Double parsedAmount = Double.parseDouble(amount);
                transaction.setAmount(parsedAmount);
                transaction.setEncryptedAmount(null);
            } catch (UnsupportedEncodingException e) {
                String logMessageFormat = "[Action=%s][Status=%s][FromEmail=%s, ToEmail=%s]";
                String logMessage = String.format(logMessageFormat, "initiateTransaction", ERR_TRANS_DECODE, fromEmail,
                        toEmail);
                logger.info(logMessage);

                return ERR_TRANS_DECODE;
            } catch (GeneralSecurityException e) {
                String logMessageFormat = "[Action=%s][Status=%s][FromEmail=%s, ToEmail=%s]";
                String logMessage = String.format(logMessageFormat, "initiateTransaction", ERR_TRANS_DECRYPTION,
                        fromEmail, toEmail);
                logger.info(logMessage);

                return ERR_TRANS_DECRYPTION;
            } catch (NumberFormatException e) {
                String logMessageFormat = "[Action=%s][Status=%s][FromEmail=%s, ToEmail=%s]";
                String logMessage = String.format(logMessageFormat, "initiateTransaction", ERR_TRANS_INCORRECT_FORMAT,
                        fromEmail, toEmail);
                logger.info(logMessage);

                return ERR_TRANS_INCORRECT_FORMAT;
            } catch (TimeExpiredException e) {
                String logMessageFormat = "[Action=%s][Status=%s][FromEmail=%s, ToEmail=%s]";
                String logMessage = String.format(logMessageFormat, "initiateTransaction", ERR_TRANS_EXPIRED, fromEmail,
                        toEmail);
                logger.info(logMessage);

                return ERR_TRANS_EXPIRED;
            } catch (Exception e) {
                String logMessageFormat = "[Action=%s][Status=%s][FromEmail=%s, ToEmail=%s]";
                String logMessage = String.format(logMessageFormat, "initiateTransaction", ERR_UNHANDLED, fromEmail,
                        toEmail);
                logger.info(logMessage);

                return ERR_UNHANDLED;
            }

            Account fromAccount = accountService.getAccountByUser(fromUser);
            Account toAccount = accountService.getAccountByUser(toUser);

            if (fromAccount == null || toAccount == null) {
                String logMessageFormat = "[Action=%s][Status=%s][FromEmail=%s, ToEmail=%s]";
                String logMessage = String.format(logMessageFormat, "initiateTransaction", ERR_ACCOUNT_NOT_EXISTS,
                        fromEmail, toEmail);
                logger.info(logMessage);

                return ERR_ACCOUNT_NOT_EXISTS;
            }

            transaction.setFromAccount(fromAccount);
            transaction.setToAccount(toAccount);
            transaction.setStatus(S_PENDING);
            transactionRepository.save(transaction);

            String logMessageFormat = "[Action=%s][Status=%s][FromEmail=%s, ToEmail=%s, Transaction=%s]";
            String logMessage = String.format(logMessageFormat, "initiateTransaction", SUCCESS, fromEmail, toEmail,
                    transaction.getTransactionId());
            logger.info(logMessage);

            return SUCCESS;
        } catch (Exception e) {
            String logMessageFormat = "[Action=%s][Status=%s][Transaction=%s, Message=%s]";
            String logMessage = String.format(logMessageFormat, "initiateTransaction", ERROR,
                    transaction.getTransactionId(), e.getMessage());
            logger.error(logMessage);

            return ERROR;
        }
    }

    @Transactional
    @Override
    public String creditDebit(String email, Transaction transaction) {
        try {

            User fromUser = userService.getUserByEmail(email);
            User toUser = fromUser;

            if (fromUser == null || toUser == null) {
                String logMessageFormat = "[Action=%s][Status=%s][Email=%s, Transaction=%s]";
                String logMessage = String.format(logMessageFormat, "creditDebit", ERR_ACCOUNT_NOT_EXISTS, email,
                        transaction.getTransactionId());
                logger.info(logMessage);

                return ERR_ACCOUNT_NOT_EXISTS;
            }

            if (fromUser.equals(toUser)) {
                String logMessageFormat = "[Action=%s][Status=%s][Email=%s, Transaction=%s]";
                String logMessage = String.format(logMessageFormat, "creditDebit", ERR_SAME_USER, email,
                        transaction.getTransactionId());
                logger.info(logMessage);

                return ERR_SAME_USER;
            }

            byte[] privateKeyBytes = fromUser.getPublicKey();

            try {
                String amount = decryptAmount(privateKeyBytes, transaction.getEncryptedAmount());
                Double parsedAmount = Double.parseDouble(amount);
                transaction.setAmount(parsedAmount);
                transaction.setEncryptedAmount(null);
            } catch (UnsupportedEncodingException e) {
                String logMessageFormat = "[Action=%s][Status=%s][Email=%s, Transaction=%s]";
                String logMessage = String.format(logMessageFormat, "creditDebit", ERR_TRANS_DECODE, email,
                        transaction.getTransactionId());
                logger.info(logMessage);

                return ERR_TRANS_DECODE;
            } catch (GeneralSecurityException e) {
                String logMessageFormat = "[Action=%s][Status=%s][Email=%s, Transaction=%s]";
                String logMessage = String.format(logMessageFormat, "creditDebit", ERR_TRANS_DECRYPTION, email,
                        transaction.getTransactionId());
                logger.info(logMessage);

                return ERR_TRANS_DECRYPTION;
            } catch (NumberFormatException e) {
                String logMessageFormat = "[Action=%s][Status=%s][Email=%s, Transaction=%s]";
                String logMessage = String.format(logMessageFormat, "creditDebit", ERR_TRANS_INCORRECT_FORMAT, email,
                        transaction.getTransactionId());
                logger.info(logMessage);

                return ERR_TRANS_INCORRECT_FORMAT;
            } catch (TimeExpiredException e) {
                String logMessageFormat = "[Action=%s][Status=%s][Email=%s, Transaction=%s]";
                String logMessage = String.format(logMessageFormat, "creditDebit", ERR_TRANS_EXPIRED, email,
                        transaction.getTransactionId());
                logger.info(logMessage);

                return ERR_TRANS_EXPIRED;
            } catch (Exception e) {
                String logMessageFormat = "[Action=%s][Status=%s][Email=%s, Transaction=%s, ErrorMessage=%s]";
                String logMessage = String.format(logMessageFormat, "creditDebit", ERR_UNHANDLED, email,
                        transaction.getTransactionId(), e.getMessage());
                logger.error(logMessage);

                return ERR_UNHANDLED;
            }

            Account fromAccount = accountService.getAccountByUser(fromUser);
            Account toAccount = fromAccount;

            String status = transaction.getStatus();

            if (status.equalsIgnoreCase("A_Credit")) {
                transaction.setStatus(A_CREDIT);
            } else {
                transaction.setStatus(A_DEBIT);

                if (fromAccount.getBalance() < transaction.getAmount()) {
                    String logMessageFormat = "[Action=%s][Status=%s][Email=%s, Transaction=%s]";
                    String logMessage = String.format(logMessageFormat, "creditDebit", ERR_LESS_BALANCE, email,
                            transaction.getTransactionId());
                    logger.info(logMessage);

                    return ERR_LESS_BALANCE;
                }
            }

            transaction.setFromAccount(fromAccount);
            transaction.setToAccount(toAccount);
            transactionRepository.save(transaction);

            String logMessageFormat = "[Action=%s][Status=%s][Email=%s, Transaction=%s]";
            String logMessage = String.format(logMessageFormat, "creditDebit", SUCCESS, email,
                    transaction.getTransactionId());
            logger.info(logMessage);

            return SUCCESS;
        } catch (Exception e) {
            String logMessageFormat = "[Action=%s][Status=%s][Email=%s, Transaction=%s, ErrorMessage=%s]";
            String logMessage = String.format(logMessageFormat, "creditDebit", ERROR, email,
                    transaction.getTransactionId(), e.getMessage());
            logger.error(logMessage);
            return ERROR;
        }
    }

    @Override
    public Transaction getTransactionsById(String id) {
        Transaction transaction = transactionRepository.findOne(id);

        String logMessageFormat = "[Action=%s][Status=%s][ID=%s, Transaction=%s]";
        String logMessage = String.format(logMessageFormat, "getTransactionsById", SUCCESS, id,
                transaction.getTransactionId());
        logger.info(logMessage);

        return transaction;
    }

    @Override
    public List<Transaction> getMerchantRequests(String status) {
        List<Transaction> transactions = transactionRepository.findByStatus(status);

        String logMessageFormat = "[Action=%s][Status=%s][Status=%s, Transactions=%s]";
        String logMessage = String.format(logMessageFormat, "getMerchantRequests", SUCCESS, status,
                transactions.size());
        logger.info(logMessage);

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

                String logMessageFormat = "[Action=%s][Status=%s][ID=%s, Status=%s, Transaction=%s]";
                String logMessage = String.format(logMessageFormat, "actionOnRequest", msg, id, status,
                        transaction.getTransactionId());
                logger.info(logMessage);

                return msg;
            }

            transactionRepository.save(transaction);

            String logMessageFormat = "[Action=%s][Status=%s][ID=%s, Status=%s, Transaction=%s]";
            String logMessage = String.format(logMessageFormat, "actionOnRequest", SUCCESS, id, status,
                    transaction.getTransactionId());
            logger.info(logMessage);

            return SUCCESS;
        } catch (Exception e) {
            String logMessageFormat = "[Action=%s][Status=%s][ID=%s, Status=%s, ErrorMessage=%s]";
            String logMessage = String.format(logMessageFormat, "actionOnRequest", SUCCESS, id, status, e.getMessage());
            logger.error(logMessage);

            return ERROR;
        }
    }

    @Transactional
    @Override
    public List<Transaction> getPendingTransactions() {

        List<Transaction> transactions = transactionRepository.findByStatus(S_PENDING);

        String logMessageFormat = "[Action=%s][Status=%s][Transactions=%s]";
        String logMessage = String.format(logMessageFormat, "getPendingTransactions", SUCCESS, transactions.size());
        logger.info(logMessage);

        return transactions;
    }

    @Transactional
    @Override
    public List<Transaction> getCreditDebitRequest() {
        List<Transaction> transactions = transactionRepository.findByStatus(A_CREDIT);
        transactions.addAll(transactionRepository.findByStatus(A_DEBIT));

        String logMessageFormat = "[Action=%s][Status=%s][Transactions=%s]";
        String logMessage = String.format(logMessageFormat, "getPendingTransactions", SUCCESS, transactions.size());
        logger.info(logMessage);

        return transactions;
    }

    @Override
    public String creditDebitTransaction(User user, String action, Transaction transaction) {
        try {
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
            if (action.equals(A_CREDIT)) {
                amount = transaction.getToAccount().getBalance() + transaction.getAmount();

            } else {
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
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    private boolean isBelowCriticalLimit(Account fromAccount, Transaction transaction) {
        double criticalLimit = fromAccount.getCriticalLimit();
        if (transaction.getAmount() >= criticalLimit) {
            return true;
        }
        return false;
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
}