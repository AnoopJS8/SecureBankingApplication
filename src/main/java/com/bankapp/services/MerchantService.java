package com.bankapp.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.models.Account;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.repositories.AccountRepository;
import com.bankapp.repositories.TransactionRepository;

@Service
public class MerchantService implements IMerchantService{
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AccountRepository accountRepository;

    @Transactional
	@Override
	public List<Transaction> getTransactionsByUserAndAccount(final User user, Account fromAccount, Account toAccount) {
		// TODO Auto-generated method stub
    	List<Transaction> list = transactionRepository.findByUserAndFromAccountOrToAccountOrderByCreatedAsc(user, fromAccount, toAccount);
		return list;
	}

	@Override
	public List<Account> getAccountsByUser(User user) {
		// TODO Auto-generated method stub
		List<Account> list = accountRepository.findByUserOrderByCreatedAsc(user);
		return list;
	}

	@Override
	public Account saveAccount(Account account) {
		Account acc = accountRepository.save(account);
		return acc;
	}
	
}
