package com.scraping.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset Verification Code");
        message.setText("Your verification code is: " + code + "\n\nThis code will expire in 15 minutes.");

        try {
            mailSender.send(message);
            log.info("Password reset email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}", to, e);
        }
    }

    public void sendExtractionCompletionEmail(String to, String fileName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Extraction Completed");
        message.setText(
                "Hello,\n\nThe extraction process for file '" + fileName + "' has been successfully completed.");

        try {
            mailSender.send(message);
            log.info("Extraction completion email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send extraction completion email to {}", to, e);
        }
    }
}
