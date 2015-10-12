package com.bankapp.repositories;

import org.springframework.data.repository.CrudRepository;

import com.bankapp.models.Account;
import com.bankapp.models.User;

public interface AccountRepository extends CrudRepository<Account, Long> {

    Account findByUser(User user);

    Account saveAndFlush(Account account);
}
