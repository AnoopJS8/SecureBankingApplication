package com.bankapp.services;

import java.util.List;

import com.bankapp.models.Requests;
import com.bankapp.models.Transaction;

public interface IRegularEmployee {

//	public List<Transaction> getTransactionByTflag(String str);

	public List<Requests> getRequest(Long id);

//	public List<Transaction> modifyTransaction(int user_ID);
	

}
