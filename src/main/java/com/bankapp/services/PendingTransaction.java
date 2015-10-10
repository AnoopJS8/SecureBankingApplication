package com.bankapp.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.repositories.TransactionRepository;
import com.bankapp.repositories.UserRepository;
@Service
public class PendingTransaction implements IPendingTransaction{
	
	@Autowired
    private UserRepository TransRepo;
	private TransactionRepository TransRepo1;

//	@Override
//	public User getUsersByUsername(String str) {
//		// TODO Auto-generated method stub
//		
//		User user = TransRepo.findByUsername(str);
//		return user;
//	}

	@Override
	public List<Transaction> getTransactionByTflag(String str) {
		// TODO Auto-generated method stub
		List<Transaction> list = TransRepo1.findByTflag(str);
		return list;
	}

}
