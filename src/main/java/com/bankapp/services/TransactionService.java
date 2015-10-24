
package com.bankapp.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.constants.Constants;
import com.bankapp.models.Account;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.repositories.TransactionRepository;

@Service
public class TransactionService implements ITransactionService, Constants {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IUserService userService;

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
        Account fromAccount = accountService.getAccountByUser(fromUser);
        Account toAccount = accountService.getAccountByUser(toUser);
        if (fromAccount == null || toAccount == null) {
            return ERR_ACCOUNT_NOT_EXISTS;
        }
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setTransferDate(new Date());

        if (fromAccount.getBalance() < transaction.getAmount()) {
            return LESS_BALANCE;
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

    @Override
    public Transaction getTransactionsById(String id) {
        return transactionRepository.findOne(id);
    }
}
