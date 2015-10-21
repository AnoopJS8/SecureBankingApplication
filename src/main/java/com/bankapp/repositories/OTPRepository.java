package com.bankapp.repositories;

import org.springframework.data.repository.CrudRepository;

import com.bankapp.models.Transaction;
import com.bankapp.models.OneTimePassword;

public interface OTPRepository extends CrudRepository<OneTimePassword, Long> {
    OneTimePassword findByValue(String value);
    OneTimePassword findByresourceIdAndResourceName(Long resourceId, String resourceName);
}