package com.bankapp.services;

import com.bankapp.models.Transaction;

public interface IMailService {
    void sendEmail(String recipientAddress, String subject, String textBody);
    void sendOTPEmail(String recipientAddress, String subject, String textBody);
}
