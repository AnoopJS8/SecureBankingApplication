package com.bankapp.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bankapp.models.Requests;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.repositories.RequestRepository;
import com.bankapp.repositories.TransactionRepository;
import com.bankapp.repositories.UserRepository;
@Service
public class RegularEmployee implements IRegularEmployee{
	

	@Autowired
	private TransactionRepository TransRepo1;
	@Autowired
	private RequestRepository RequestRepo;

//	@Override
//	public User getUsersByUsername(String str) {
//		// TODO Auto-generated method stub
//		
//		User user = TransRepo.findByUsername(str);
//		return user;
//	}

//	@Override
//	public List<Transaction> getTransactionByTflag(String str) {
//		// TODO Auto-generated method stub
//		List<Transaction> list = TransRepo1.findByTflag(str);
//		return list;
//	}

	@Override
	public List<Requests> getRequest(Long id) {
		
		System.out.println("id" + id);
		// TODO Auto-generated method stub
		List<Requests> list1=RequestRepo.findById(id);
		
		return list1;
	}

}
