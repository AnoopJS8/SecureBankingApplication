package com.bankapp.services;

import java.util.List;

import com.bankapp.models.Account;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;

public interface ITransactionService {

    public List<Transaction> getTransactionsByAccount(Account fromAccount, Account toAccount);

    public String saveTransaction(String fromEmail, String toEmail, Transaction transaction);

    public String initiateTransaction(String fromEmail, String toEmail, Transaction transaction);

    public Transaction getTransactionsById(String id);

    public String askCustomerPayment(Transaction transaction, User user);

}
