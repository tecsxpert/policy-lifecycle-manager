package com.internship.tool.scheduler;

import com.internship.tool.entity.Policy;
import com.internship.tool.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PolicySchedulerService {

    @Autowired
    private PolicyRepository policyRepository;

    /**
     * Daily at 1:00 AM: Find policies that are not COMPLETED and whose deadline has
     * passed.
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void checkOverduePolicies() {
        LocalDate today = LocalDate.now();
        List<Policy> overduePolicies = policyRepository.findByStatusNotAndDeadlineBefore("COMPLETED", today);

        if (overduePolicies.isEmpty()) {
            System.out.println("[Overdue Check] No overdue policies found.");
        } else {
            System.out.println("[Overdue Check] Found " + overduePolicies.size() + " overdue policy(ies):");
            overduePolicies.forEach(
                    p -> System.out.println("  - " + p.getPolicyName() + " (deadline: " + p.getDeadline() + ")"));
        }
    }

    /**
     * Daily at 2:00 AM: Find policies with a deadline exactly 7 days from today.
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void checkExpiringSoonPolicies() {
        LocalDate sevenDaysFromNow = LocalDate.now().plusDays(7);
        List<Policy> expiringSoon = policyRepository.findByDeadline(sevenDaysFromNow);

        if (expiringSoon.isEmpty()) {
            System.out.println("[Expiring Soon Check] No policies expiring on " + sevenDaysFromNow + ".");
        } else {
            System.out.println("[Expiring Soon Check] Found " + expiringSoon.size() + " policy(ies) expiring on "
                    + sevenDaysFromNow + ":");
            expiringSoon.forEach(p -> System.out.println("  - " + p.getPolicyName()));
        }
    }

    /**
     * Every Monday at 9:00 AM: Generate a weekly summary of policy activity.
     */
    @Scheduled(cron = "0 0 9 * * MON")
    public void generateWeeklySummary() {
        long totalPolicies = policyRepository.count();
        long activePolicies = policyRepository.countByStatus("Active");
        long pendingPolicies = policyRepository.countByStatus("Pending");

        System.out.println("===== Weekly Policy Summary =====");
        System.out.println("Total Policies : " + totalPolicies);
        System.out.println("Active Policies: " + activePolicies);
        System.out.println("Pending Policies: " + pendingPolicies);
        System.out.println("=================================");
    }
}
