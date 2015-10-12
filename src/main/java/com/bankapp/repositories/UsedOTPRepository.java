package com.bankapp.repositories;

import org.springframework.data.repository.CrudRepository;

import com.bankapp.models.Transaction;
import com.bankapp.models.OTPVerification;

public interface UsedOTPRepository extends CrudRepository<OTPVerification, Long> {
    OTPVerification findByUsedOTP(String usedOTP);

    OTPVerification findByTransaction(Transaction transaction);
}