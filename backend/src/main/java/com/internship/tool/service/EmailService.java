package com.internship.tool.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendPolicyCreatedEmail(String to, String policyName) {
        sendPolicyCreatedEmail(to, policyName, "N/A", "UNKNOWN");
    }

    public void sendPolicyCreatedEmail(String to, String policyName, String policyNumber, String status) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Policy Created Successfully");

            Context context = new Context();
            context.setVariable("policyName", policyName);
            context.setVariable("policyNumber", policyNumber);
            context.setVariable("status", status);

            String html = templateEngine.process("policy-created", context);
            helper.setText(html, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("Unable to send policy created email to {}", to, e);
        }
    }

    public void sendExpiryReminder(String to, String policyName, String policyNumber, String status,
            LocalDate expiryDate) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Policy Expiry Reminder");

            Context context = new Context();
            context.setVariable("policyName", policyName);
            context.setVariable("policyNumber", policyNumber);
            context.setVariable("status", status);
            context.setVariable("expiryDate", expiryDate.format(DATE_FORMATTER));

            String html = templateEngine.process("policy-expiry-reminder", context);
            helper.setText(html, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("Unable to send expiry reminder email to {}", to, e);
        }
    }
}
