package com.internship.tool.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPolicyCreatedEmail(String to, String policyName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Policy Created Successfully");
        message.setText("Your policy '" + policyName + "' has been created successfully.");
        mailSender.send(message);
    }
}