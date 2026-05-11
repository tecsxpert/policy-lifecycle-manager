package com.internship.tool.config;

import com.internship.tool.entity.Policy;
import com.internship.tool.entity.PolicyStatus;
import com.internship.tool.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * DataLoader: Automatically seeds demo policies on application startup.
 * Prevents duplicate insertion on restart by checking if data already exists.
 */
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final PolicyRepository policyRepository;
    private static final Random random = new Random();

    /**
     * Policy types for realistic variety
     */
    private static final String[] POLICY_TYPES = {
            "Health",
            "Vehicle",
            "Travel",
            "Life",
            "Home"
    };

    /**
     * Policy names corresponding to types
     */
    private static final String[] POLICY_NAMES = {
            "Health Insurance Premium",
            "Comprehensive Car Insurance",
            "International Travel Shield",
            "Life Protection Plan",
            "Home & Property Secure"
    };

    /**
     * Run method called on application startup
     */
    @Override
    public void run(String... args) throws Exception {
        // Prevent duplicate seeding on restart
        if (policyRepository.count() > 0) {
            System.out.println("✅ Database already contains policies. Skipping data seeding.");
            return;
        }

        List<Policy> policies = generateDemoPolicies(30);
        policyRepository.saveAll(policies);
        System.out.println("✅ Demo policies seeded successfully! Total: " + policies.size());
    }

    /**
     * Generate demo policies with randomized data
     *
     * @param count Number of policies to generate
     * @return List of generated policies
     */
    private List<Policy> generateDemoPolicies(int count) {
        List<Policy> policies = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 1; i <= count; i++) {
            // Generate unique policy number
            String policyNumber = String.format("POL%04d", 1000 + i);

            // Randomize policy type and name
            int typeIndex = random.nextInt(POLICY_TYPES.length);
            String policyType = POLICY_TYPES[typeIndex];
            String policyName = POLICY_NAMES[typeIndex] + " - " + policyNumber;

            // Randomize premium amount (1000 - 10000)
            BigDecimal premiumAmount = BigDecimal.valueOf(1000 + random.nextInt(9001));

            // Randomize start date (within last 2 years)
            LocalDate startDate = today.minusDays(random.nextInt(730));

            // Randomize end date duration (6 months to 3 years)
            LocalDate endDate = startDate.plusDays(180 + random.nextInt(660));

            // Ensure coverage for active, pending, and expired statuses
            PolicyStatus status = randomStatus(i);

            if (status == PolicyStatus.EXPIRED && !endDate.isBefore(today)) {
                endDate = today.minusDays(random.nextInt(30) + 1);
            }
            if (status == PolicyStatus.PENDING && !endDate.isAfter(today)) {
                endDate = today.plusDays(random.nextInt(30) + 1);
            }
            if (status == PolicyStatus.ACTIVE && !endDate.isAfter(today)) {
                endDate = today.plusDays(random.nextInt(365) + 1);
            }

            // Build and add policy
            Policy policy = Policy.builder()
                    .policyNumber(policyNumber)
                    .policyName(policyName)
                    .policyType(policyType)
                    .premiumAmount(premiumAmount)
                    .startDate(startDate)
                    .endDate(endDate)
                    .status(status)
                    .build();

            policies.add(policy);
        }

        return policies;
    }

    /**
     * Return a random policy status, while guaranteeing coverage for core statuses.
     *
     * @param index Position within generated policies
     * @return Selected PolicyStatus
     */
    private PolicyStatus randomStatus(int index) {
        if (index == 1) {
            return PolicyStatus.ACTIVE;
        }
        if (index == 2) {
            return PolicyStatus.PENDING;
        }
        if (index == 3) {
            return PolicyStatus.EXPIRED;
        }

        PolicyStatus[] statuses = PolicyStatus.values();
        return statuses[random.nextInt(statuses.length)];
    }
}
