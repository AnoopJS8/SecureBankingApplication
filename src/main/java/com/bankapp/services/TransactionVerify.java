package com.bankapp.services;

import com.bankapp.models.Account;
import com.bankapp.models.Transaction;


public class TransactionVerify implements ITransaction{


	public String TransactionVerifyDetails(Transaction t) {
		// TODO Auto-generated method stub
		
		Account acc = t.getFromAccount();
		Double amount = t.getAmount();
		Double balance = acc.getBalance();
		if(balance>amount)
		{
			return "Authorize";
		}
		else
		{
			return "Not authorized";
		}
		
		
	}

	

	

}
