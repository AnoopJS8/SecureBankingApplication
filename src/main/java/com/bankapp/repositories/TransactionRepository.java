package com.bankapp.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.bankapp.models.Account;
import com.bankapp.models.Transaction;


public interface TransactionRepository extends CrudRepository<Transaction, String> {


    List<Transaction> findByFromAccountOrToAccountOrderByCreatedAsc(Account fromAccount, Account toAccount);

    Transaction findByTransactionId(String id);

    List<Transaction> findByStatus(String str);
    
    List<Transaction> findByFromAccount(Account fromAccount);
    
    List<Transaction> findByToAccount(Account toAccount);
    
    
}

