package com.internship.tool.service;

public class NoOpEmailService extends EmailService {
    @Override
    public void sendPolicyCreatedEmail(String to, String policyName, String policyHolder) {
        // no-op
    }

    @Override
    public void sendOverdueReminderEmail(String to, String policyName, String policyHolder) {
        // no-op
    }

    @Override
    public void sendExpiringSoonEmail(String to, String policyName, String policyHolder, int daysRemaining) {
        // no-op
    }
}
