package com.bankapp.repositories;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bankapp.models.User;

@Repository
@Qualifier(value = "userRepository")
public interface UserRepository extends CrudRepository<User, Long> {
    public User findByEmail(String email);
}