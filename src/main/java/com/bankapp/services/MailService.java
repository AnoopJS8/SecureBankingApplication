package com.bankapp.services;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MailService implements IMailService {

    private final Logger logger = Logger.getLogger(MailService.class);

    @Value("${com.bankapp.email.mail_username}")
    private String username;

    @Value("${com.bankapp.email.mail_password}")
    private String password;

    @Override
    public void sendEmail(String recipientAddress, String subject, String textBody) {
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

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("my-asu-bank@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientAddress));
            message.setSubject(subject);
            message.setContent(textBody, "text/html; charset=utf-8");

            Transport.send(message);

            String logMessageFormat = "[Action=%s][Recipient=%s, Subject=%s]";
            String logMessage = String.format(logMessageFormat, "sendEmail", recipientAddress, subject);
            logger.info(logMessage);

        } catch (MessagingException e) {
            String logMessageFormat = "[Action=%s][Recipient=%s, Subject=%s, ErrorMessage=%s]";
            String logMessage = String.format(logMessageFormat, "sendEmail", recipientAddress, subject, e.getMessage());
            logger.error(logMessage);

            throw new RuntimeException(e);
        }
    }

}
