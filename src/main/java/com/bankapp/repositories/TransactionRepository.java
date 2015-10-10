package com.bankapp.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.bankapp.models.Transaction;
import com.bankapp.models.User;

public interface TransactionRepository extends CrudRepository<Transaction, Long>{

	List<Transaction> findByUserOrderByCreatedAsc(User user );

	List<Transaction> findByTflag(String str);
	
	
}
