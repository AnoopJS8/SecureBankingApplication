package com.bankapp.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.bankapp.models.OneTimePassword;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.services.IMailService;
import com.bankapp.services.IUserService;

@Component
public class TemporaryListener implements ApplicationListener<OnCriticalTransaction> {
    @Autowired
    private IUserService oTPService;

    @Autowired
    private IMailService mailService;

    @Override
    public void onApplicationEvent(OnCriticalTransaction event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnCriticalTransaction event) {
        Transaction transaction = event.getTransaction();
        OneTimePassword otp = oTPService.generateOTP(transaction);
        User recipientUser = transaction.getFromAccount().getUser();
        String subject = "My ASU Bank - OTP";
        long transactionID = transaction.getTransactionId();
        String recipientUsername = recipientUser.getUsername();
        String recipientEmail = recipientUser.getEmail();
        String textBody = String.format("Dear valued customer <b>%s</b>, <br><br>"
                + "Please find the One Time Password for the transaction# %d: %s<br />"
                + "Regards,<br />My ASU Bank<br>", recipientUsername, transactionID, otp.getValue());

        mailService.sendEmail(recipientEmail, subject, textBody);
    }
}