package com.example.email_service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

/**
 * Сервис для работы с электронной почтой.
 */
@Service
@RequiredArgsConstructor
public class MailService {
    private final MailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Отправляет ссылку для верификации.
     *
     * @param email  email адрес
     * @param verificationLink  ссылка для верификации
     */
    public void sendVerificationLink(String email, String verificationLink) {
        var finalMessage = "Please follow this link to finish registration: " + verificationLink;
        sendMessage(email, "Verification link", finalMessage);
    }

    private void sendMessage(String email, String subject, String text) {
        var simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromEmail);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(text);

        try {
            mailSender.send(simpleMailMessage);
        } catch (MailException e) {
            throw new com.example.email_service.exceptions.MailException(e.getMessage(), e);
        }
    }
}
