package com.bankapp.services;

import java.util.List;

import com.bankapp.exceptions.EmailDoesNotExist;
import com.bankapp.exceptions.EmailExistsException;
import com.bankapp.exceptions.UserAlreadyExistException;
import com.bankapp.exceptions.UserIdDoesNotExist;
import com.bankapp.exceptions.UserNameExistsException;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;

public interface ISystemManagerService {
	
//	public User getUsersByUsername(String str);
	
	public List<Transaction> getTransactionByTflag(String str);
	
	public User createUser(User user) throws EmailExistsException, UserNameExistsException, UserAlreadyExistException;
	
	public User viewUserById(Long id)throws UserIdDoesNotExist;
	
	public User viewUserByEmail(String email)throws EmailDoesNotExist;

}
