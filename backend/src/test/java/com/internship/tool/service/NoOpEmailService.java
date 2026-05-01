package com.internship.tool.service;

/**
 * No-op subclass of EmailService for test environments.
 * Prevents Java 25 Mockito inline mocking issues by avoiding
 * the need to mock EmailService directly.
 */
public class NoOpEmailService extends EmailService {

    @Override
    public void sendPolicyCreatedEmail(String to, String policyName, String policyHolder) {
        // No-op: suppress email sending in tests
    }

    @Override
    public void sendOverdueReminderEmail(String to, String policyName, String policyHolder) {
        // No-op: suppress email sending in tests
    }

    @Override
    public void sendExpiringSoonEmail(String to, String policyName, String policyHolder, int daysRemaining) {
        // No-op: suppress email sending in tests
    }
}
