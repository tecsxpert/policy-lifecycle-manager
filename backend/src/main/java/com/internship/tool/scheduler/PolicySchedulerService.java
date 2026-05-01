package com.internship.tool.scheduler;

import com.internship.tool.entity.Policy;
import com.internship.tool.repository.PolicyRepository;
import com.internship.tool.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PolicySchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(PolicySchedulerService.class);

    /** Maximum number of policies to load into memory per scheduler run. */
    private static final int MAX_SCHEDULER_RESULTS = 500;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Daily at 1:00 AM: Find policies that are not COMPLETED and whose expiry_date
     * is in the past. Capped at 500 results to prevent OOM during live demo.
     */
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional(readOnly = true)
    public void checkOverduePolicies() {
        LocalDate today = LocalDate.now();
        Pageable limit = PageRequest.of(0, MAX_SCHEDULER_RESULTS);
        List<Policy> overduePolicies = policyRepository.findByStatusNotAndExpiryDateBefore("COMPLETED", today, limit);

        if (overduePolicies.isEmpty()) {
            logger.info("[Overdue Check] No overdue policies found.");
        } else {
            logger.info("[Overdue Check] Found {} overdue policy(ies):", overduePolicies.size());
            overduePolicies.forEach(p -> {
                logger.info("  - {} (expiry_date: {})", p.getPolicyName(), p.getExpiryDate());
                emailService.sendOverdueReminderEmail("admin@policy.local", p.getPolicyName(), p.getPolicyHolder());
            });
        }
    }

    /**
     * Daily at 2:00 AM: Find policies with an expiry_date exactly 7 days from
     * today. Capped at 500 results to prevent OOM during live demo.
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional(readOnly = true)
    public void checkExpiringSoonPolicies() {
        LocalDate sevenDaysFromNow = LocalDate.now().plusDays(7);
        Pageable limit = PageRequest.of(0, MAX_SCHEDULER_RESULTS);
        List<Policy> expiringSoon = policyRepository.findByExpiryDate(sevenDaysFromNow, limit);

        if (expiringSoon.isEmpty()) {
            logger.info("[Expiring Soon Check] No policies expiring on {}.", sevenDaysFromNow);
        } else {
            logger.info("[Expiring Soon Check] Found {} policy(ies) expiring on {}:",
                    expiringSoon.size(), sevenDaysFromNow);
            expiringSoon.forEach(p -> {
                logger.info("  - {}", p.getPolicyName());
                emailService.sendExpiringSoonEmail("admin@policy.local", p.getPolicyName(), p.getPolicyHolder(), 7);
            });
        }
    }

    /**
     * Every Monday at 9:00 AM: Generate a weekly summary of policy activity.
     * Excludes soft-deleted policies to ensure accurate reporting.
     */
    @Scheduled(cron = "0 0 9 * * MON")
    @Transactional(readOnly = true)
    public void generateWeeklySummary() {
        long totalPolicies = policyRepository.countByIsDeletedFalse();
        long activePolicies = policyRepository.countByStatusAndIsDeletedFalse("Active");
        long pendingPolicies = policyRepository.countByStatusAndIsDeletedFalse("Pending");

        logger.info("===== Weekly Policy Summary =====");
        logger.info("Total Policies (excl. deleted) : {}", totalPolicies);
        logger.info("Active Policies: {}", activePolicies);
        logger.info("Pending Policies: {}", pendingPolicies);
        logger.info("=================================");
    }
}
