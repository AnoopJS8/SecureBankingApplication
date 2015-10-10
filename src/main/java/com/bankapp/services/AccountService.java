package com.bankapp.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.constants.Constants;
import com.bankapp.models.Account;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.repositories.AccountRepository;

@Service
public class AccountService implements IAccountService, Constants{
	
	@Autowired
    private AccountRepository accountRepository;

	@Transactional
	@Override
	public Account getAccountsByUser(User user) {
		Account account = accountRepository.findByUser(user);
		return account;
	}

	@Transactional
	@Override
	public Account saveAccount(Account account) {
		Account acc = accountRepository.save(account);
		return acc;
	}

	@Transactional
	@Override
	public Account getAccount(Long accId) {
		Account acc = accountRepository.findByAccId(accId);
		return acc;
	}

	@Transactional
	@Override
	public String updateBalance(Transaction transaction) {
		try{
			double amount  = transaction.getAmount();
			if (transaction.getFromAccount().getBalance()<amount){
				return LESS_BALANCE;
			}
			transaction.getToAccount().setBalance(transaction.getToAccount().getBalance() + amount);
			transaction.getFromAccount().setBalance(transaction.getFromAccount().getBalance() - amount);
			accountRepository.saveAndFlush(transaction.getToAccount());
			accountRepository.saveAndFlush(transaction.getFromAccount());
			return SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	

}
