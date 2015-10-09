package com.bankapp.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.models.Account;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.repositories.TransactionRepository;

@Service
public class TransactionService implements ITransactionService{
	
	@Autowired
    private TransactionRepository transactionRepository;

	@Transactional
	@Override
	public List<Transaction> getTransactionsByUserAndAccount(User user, Account fromAccount, Account toAccount) {
		List<Transaction> list = transactionRepository.findByUserAndFromAccountOrToAccountOrderByCreatedAsc(user, fromAccount, toAccount);
		return list;
	}

}
