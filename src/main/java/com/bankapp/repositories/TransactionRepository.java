package com.bankapp.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.bankapp.models.Account;
import com.bankapp.models.Transaction;


public interface TransactionRepository extends CrudRepository<Transaction, Long> {


    List<Transaction> findByFromAccountOrToAccountOrderByCreatedAsc(Account fromAccount, Account toAccount);

    Transaction findByTransactionId(Long id);

    List<Transaction> findByStatus(String str);
}

