package com.bankapp.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.bankapp.models.Account;
import com.bankapp.models.User;

public interface AccountRepository extends CrudRepository<Account, Long>{

	List<Account> findByUserOrderByCreatedAsc(User user );
	
}
