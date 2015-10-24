package com.bankapp.services;

import java.util.Date;
import java.util.List;

import com.bankapp.exceptions.EmailDoesNotExist;
import com.bankapp.exceptions.EmailExistsException;
import com.bankapp.exceptions.UserAlreadyExistException;
import com.bankapp.exceptions.UserIdDoesNotExist;
import com.bankapp.exceptions.UserNameExistsException;
import com.bankapp.models.Account;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;

public interface ISystemManagerService {
	
	
	public List<Transaction> getTransactionsByStatus(String status);
	
	public User addUser(User user) throws EmailExistsException, UserNameExistsException, UserAlreadyExistException;
	
	public User viewUserById(String id);
	
	public User viewUserByEmail(String email);
	
	public String approveTransaction(Transaction transaction);
	
	public Transaction getTransactionbyid(String string);
	
	public String reflectChangesToSender(Account account, Double balance, Double amount);
	
	public String reflectChangesToReceiver(Account account, Double balance, Double amount);

	public String declineTransaction(Transaction transaction);

	public String modifyTransaction(Transaction transaction, Date new_date);

	

}