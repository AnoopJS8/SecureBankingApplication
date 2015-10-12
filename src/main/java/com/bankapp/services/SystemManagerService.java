package com.bankapp.services;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bankapp.controllers.SignupController;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.repositories.TransactionRepository;
import com.bankapp.repositories.UserRepository;
@Service
public class SystemManagerService implements ISystemManagerService{
	
	@Autowired
    private UserRepository UserRepo;
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
	
	public User viewUserById(Long id)
	{
		User user = UserRepo.findById(id);
		return user;
	}
	
	public User viewUserByEmail(String email)
	{
		User user = UserRepo.findByEmail(email);
		return user;
	}
	
	public User createUser(User user)
	{	   
		UserRepo.save(user);
		return user;
	}

}
