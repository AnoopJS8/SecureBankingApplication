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
	public List<Transaction> getTransactionsByUser(final User user) {
		// TODO Auto-generated method stub
    	List<Transaction> list = transactionRepository.findByUserOrderByCreatedAsc(user);
		return list;
	}

	@Override
	public List<Account> getAccountByUser(User user) {
		// TODO Auto-generated method stub
		List<Account> list = accountRepository.findByUserOrderByCreatedAsc(user);
		return list;
	}
	
}
