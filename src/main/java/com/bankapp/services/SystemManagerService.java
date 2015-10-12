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
	private TransactionRepository TransRepo;
	
 
	@Override
	public List<Transaction> getTransactionByStatus(String status) {
		// TODO Auto-generated method stub
		List<Transaction> list = TransRepo.findByStatus(status);
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
	
	public User addUser(User user)
	{	   
		UserRepo.save(user);
		return user;
	}

}
