package com.bankapp.services;

import java.util.List;

import com.bankapp.models.Account;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;

public interface ITransactionService {

	public List<Transaction> getTransactionsByAccount(Account fromAccount, Account toAccount);
	public String saveTransaction(Transaction transaction, User user);
}