package com.bankapp.repositories;

import org.springframework.data.repository.CrudRepository;

import com.bankapp.models.User;

public interface MerchantRepository extends CrudRepository<User, Long>{

}
