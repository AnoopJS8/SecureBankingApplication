package com.bankapp.listeners;

import java.util.Properties;
import java.util.UUID;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.bankapp.models.User;
import com.bankapp.services.IUserService;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    @Autowired
    private IUserService service;

    @Value("${com.bankapp.email.mail_username}")
    private String username;

    @Value("${com.bankapp.email.mail_password}")
    private String password;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.createVerificationToken(user, token);
        String recipientAddress = user.getEmail();
        String subject = "My ASU Bank - Registration Confirmation";
        String confirmationUrl = "http://localhost:8081/registrationConfirm?token=" + token;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            String recipientUsername = user.getUsername();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("my-asu-bank@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientAddress));
            message.setSubject(subject);
            String textBody = String.format(
                    "Dear valued customer <b>%s</b>, <br><br>"
                            + "Thank you for registering with our My ASU Bank.<br />To activate your account, "
                            + "please click on <b><a href='%s' target='_blank'>this</a></b> link.<br><br>"
                            + "Regards,<br />My ASU Bank<br>",
                    recipientUsername, confirmationUrl);
            message.setContent(textBody, "text/html; charset=utf-8");

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}