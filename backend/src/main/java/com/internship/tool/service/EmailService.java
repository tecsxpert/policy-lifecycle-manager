package com.internship.tool.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Async
    public void sendPolicyCreatedEmail(String to, String policyName, String policyHolder) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Policy Created: " + policyName);
            message.setText("Hello " + policyHolder + ",\n\nYour policy \"" + policyName + "\" has been successfully created in the Policy Lifecycle Manager.\n\nRegards,\nPolicy Team");
            mailSender.send(message);
            logger.info("Sent policy created email to {} for policy {}", to, policyName);
        } catch (Exception e) {
            logger.error("Failed to send policy created email to {}: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendOverdueReminderEmail(String to, String policyName, String policyHolder) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("URGENT: Policy Overdue — " + policyName);
            message.setText("Hello " + policyHolder + ",\n\nYour policy \"" + policyName + "\" is now overdue. Please renew it immediately to avoid coverage lapse.\n\nRegards,\nPolicy Team");
            mailSender.send(message);
            logger.info("Sent overdue reminder email to {} for policy {}", to, policyName);
        } catch (Exception e) {
            logger.error("Failed to send overdue reminder email to {}: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendExpiringSoonEmail(String to, String policyName, String policyHolder, int daysRemaining) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Reminder: Policy Expiring in " + daysRemaining + " Days — " + policyName);
            message.setText("Hello " + policyHolder + ",\n\nYour policy \"" + policyName + "\" will expire in " + daysRemaining + " days. Please renew before the expiry date to maintain continuous coverage.\n\nRegards,\nPolicy Team");
            mailSender.send(message);
            logger.info("Sent expiring soon email to {} for policy {}", to, policyName);
        } catch (Exception e) {
            logger.error("Failed to send expiring soon email to {}: {}", to, e.getMessage());
        }
    }
}

