package com.bankapp.services;

import java.util.List;

import com.bankapp.models.Account;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;

public interface ICustomerService {
	
	public List<Transaction> getTransactionsByUser(User user);
	public List<Account> getAccountsByUser(User user);

}
