package com.bankapp.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.constants.Constants;
import com.bankapp.models.Account;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.repositories.TransactionRepository;

@Service
public class TransactionService implements ITransactionService, Constants{
	
	@Autowired
    private TransactionRepository transactionRepository;
	
	@Autowired

    private IUserService userService;

    private IAccountService accountService;

	

	@Transactional
	@Override
	public List<Transaction> getTransactionsByAccount(Account fromAccount, Account toAccount) {
		List<Transaction> list = transactionRepository.findByFromAccountOrToAccountOrderByCreatedAsc( fromAccount, toAccount);
		return list;
	}

	@Transactional
	@Override
	public String saveTransaction(Transaction transaction, User user) {
		try{
			Long userId = transaction.getToAccount().getUser().getId();
			transaction.setToAccount(accountService.getAccountsByUser(userService.getUserById(userId)));
			transaction.setFromAccount(accountService.getAccountsByUser(user));
			transaction.setUser(user);
			String message = accountService.updateBalance(transaction);
			if(message!=null){
				if(message.equalsIgnoreCase(LESS_BALANCE)){
					return LESS_BALANCE;
				}else{
					transactionRepository.save(transaction);
					return message;
				}
				
			}			
		}catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		
		return null;
	}

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

	@Override
	public List<Transaction> transactiondisplay() {
		// TODO Auto-generated method stub
		List<Transaction> t = (List<Transaction>) transactionRepository.findByStatus("verif");
		
		return t;
		
	}

	@Override
	public void TransactionVerifyDetails(long id) {
		// TODO Auto-generated method stub
		Transaction t = transactionRepository.findByTransactionId(id);
		t.setStatus("verified");
		transactionRepository.save(t);
		
	}

	

	}

	

