package com.bankapp.services;

import com.bankapp.models.Account;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;

public interface IAccountService {
	public Account getAccountsByUser(User user);
	public Account saveAccount(Account account);
	public Account getAccount(Long accId);
	public String updateBalance(Transaction transaction);
}
