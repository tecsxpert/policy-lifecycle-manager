package com.internship.tool.scheduler;

import com.internship.tool.entity.Policy;
import com.internship.tool.entity.PolicyStatus;
import com.internship.tool.repository.PolicyRepository;
import com.internship.tool.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PolicyExpiryScheduler {

    private final PolicyRepository policyRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 9 * * ?")
    public void sendExpiryReminders() {
        LocalDate today = LocalDate.now();
        LocalDate reminderThreshold = today.plusDays(7);

        try {
            List<Policy> expiringPolicies = policyRepository.findAll().stream()
                    .filter(policy -> policy.getStatus() != PolicyStatus.EXPIRED)
                    .filter(policy -> !policy.getEndDate().isBefore(today))
                    .filter(policy -> !policy.getEndDate().isAfter(reminderThreshold))
                    .toList();

            if (expiringPolicies.isEmpty()) {
                log.info("No expiring policies found for reminder today.");
                return;
            }

            for (Policy policy : expiringPolicies) {
                try {
                    emailService.sendExpiryReminder(
                            "knowmore089@gmail.com",
                            policy.getPolicyName(),
                            policy.getPolicyNumber(),
                            policy.getStatus().name(),
                            policy.getEndDate());
                } catch (Exception ex) {
                    log.error("Failed to send expiry reminder for policy {}", policy.getPolicyNumber(), ex);
                }
            }

            log.info("Expiry reminder emails sent successfully for {} expiring policy(ies).", expiringPolicies.size());
        } catch (Exception ex) {
            log.error("Expiry reminder scheduler encountered an error", ex);
        }
    }
}
