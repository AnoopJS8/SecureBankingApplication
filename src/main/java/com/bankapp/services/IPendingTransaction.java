package com.bankapp.services;

import java.util.List;

import com.bankapp.models.Transaction;
import com.bankapp.models.User;

public interface IPendingTransaction {
	
//	public User getUsersByUsername(String str);
	
	public List<Transaction> getTransactionByTflag(String str);

}
