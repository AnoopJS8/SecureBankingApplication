package com.bankapp.repositories;

import org.springframework.data.repository.CrudRepository;

import com.bankapp.models.User;
import com.bankapp.models.VerificationToken;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);
}