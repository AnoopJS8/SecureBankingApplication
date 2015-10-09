package com.bankapp.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.models.Account;
import com.bankapp.models.User;
import com.bankapp.repositories.AccountRepository;

@Service
public class AccountService implements IAccountService{
	
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

}
