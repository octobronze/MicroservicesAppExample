package com.example.email_service.services;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private static final String FROM_EMAIL = "octobronze@gmail.com";

    private final MailSender mailSender;

    public MailService(JavaMailSender javaMailSender) {
        this.mailSender = javaMailSender;
    }

    public void sendVerificationLink(String email, String verificationLink) {
        String finalMessage = "Please follow this link to finish registration: " + verificationLink;

        sendMessage(email, "Verification link", finalMessage);
    }

    private void sendMessage(String email, String subject, String text) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(FROM_EMAIL);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(text);

        try {
            mailSender.send(simpleMailMessage);
        } catch (MailException e) {
            throw new com.example.email_service.exceptions.MailException(e.getMessage());
        }
    }
}
