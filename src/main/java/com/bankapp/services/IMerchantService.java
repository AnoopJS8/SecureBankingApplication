package com.bankapp.services;

import java.util.List;

import com.bankapp.models.Account;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;

public interface IMerchantService {
	
	public List<Transaction> getTransactionsByUserAndAccount(User user, Account fromAccount, Account toAccount);
	public List<Account> getAccountsByUser(User user);
	public Account saveAccount(Account account);

}
