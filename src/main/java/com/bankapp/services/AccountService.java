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
public class AccountService implements IAccountService, Constants {

    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    @Override
    public Account getAccountByUser(User user) {
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
    public String updateBalance(Transaction transaction) {
        double amount = transaction.getAmount();
        Account fromAccount = transaction.getFromAccount();
        Account toAccount = transaction.getToAccount();
        toAccount.setBalance(toAccount.getBalance() + amount);
        fromAccount.setBalance(fromAccount.getBalance() - amount);
        accountRepository.saveAndFlush(toAccount);
        accountRepository.saveAndFlush(fromAccount);
        return SUCCESS;
    }

    @Override
    public Account getAccountByAccountId(String id) {
        return accountRepository.findOne(id);
    }
}
