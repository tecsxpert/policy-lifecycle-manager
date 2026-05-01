package com.internship.tool.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Async
    public void sendPolicyCreatedEmail(String to, String policyName, String policyHolder) {
        try {
            Context context = new Context();
            context.setVariable("policyName", policyName);
            context.setVariable("policyHolder", policyHolder);
            String htmlContent = templateEngine.process("email/policy-created", context);

            sendHtmlEmail(to, "Policy Created: " + policyName, htmlContent);
            logger.info("Sent policy created email to {} for policy {}", to, policyName);
        } catch (Exception e) {
            logger.error("Failed to send policy created email to {}: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendOverdueReminderEmail(String to, String policyName, String policyHolder) {
        try {
            Context context = new Context();
            context.setVariable("policyName", policyName);
            context.setVariable("policyHolder", policyHolder);
            String htmlContent = templateEngine.process("email/overdue-reminder", context);

            sendHtmlEmail(to, "URGENT: Policy Overdue — " + policyName, htmlContent);
            logger.info("Sent overdue reminder email to {} for policy {}", to, policyName);
        } catch (Exception e) {
            logger.error("Failed to send overdue reminder email to {}: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendExpiringSoonEmail(String to, String policyName, String policyHolder, int daysRemaining) {
        try {
            Context context = new Context();
            context.setVariable("policyName", policyName);
            context.setVariable("policyHolder", policyHolder);
            context.setVariable("daysRemaining", daysRemaining);
            String htmlContent = templateEngine.process("email/expiring-soon", context);

            sendHtmlEmail(to, "Reminder: Policy Expiring in " + daysRemaining + " Days — " + policyName, htmlContent);
            logger.info("Sent expiring soon email to {} for policy {}", to, policyName);
        } catch (Exception e) {
            logger.error("Failed to send expiring soon email to {}: {}", to, e.getMessage());
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
}
