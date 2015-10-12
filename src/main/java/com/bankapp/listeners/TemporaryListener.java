package com.bankapp.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.bankapp.models.Transaction;
import com.bankapp.services.IMailService;
import com.bankapp.services.IUserService;
import com.bankapp.validators.OTPGenerator;

@Component
public class TemporaryListener implements ApplicationListener<OnCriticalTransaction> {
    @Autowired
    private IUserService service;
    
    @Autowired
    private IMailService mailService;

    @Override
    public void onApplicationEvent(OnCriticalTransaction event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnCriticalTransaction event) {
        Transaction transaction = event.getTransaction();
        String otp = OTPGenerator.generateOTP();
        
        service.generateOTP(transaction, otp);
        String recipientAddress = transaction.getUser().getEmail();
        String subject = "My ASU Bank - OTP";
        String confirmationUrl = "http://localhost:8081/registrationConfirm?token=" + otp;
        long transactionID = transaction.getTransactionId();
        String recipientUsername = transaction.getUser().getUsername();
        String textBody = String.format("Dear valued customer <b>%s</b>, <br><br>"
                + "Please find the One Time Password for the transaction# %d: %s<br />"
                + "To validate the transaction, please click on <b><a href='%s' target='_blank'>this</a></b> link.<br><br>"
                + "Regards,<br />My ASU Bank<br>", recipientUsername, transactionID, otp, confirmationUrl);

        mailService.sendOTPEmail(recipientAddress, subject, textBody);
    }
}