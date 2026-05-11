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

        List<Policy> policies = generateDemoPolicies(25);
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
            BigDecimal premiumAmount = new BigDecimal(1000 + random.nextInt(9001));

            // Randomize start date (within last 2 years)
            LocalDate startDate = today.minusDays(random.nextInt(730));

            // Randomize end date (1-3 years after start date)
            LocalDate endDate = startDate.plusDays(365 + random.nextInt(730));

            // Randomize status
            PolicyStatus status = randomStatus();

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
     * Return a random policy status
     *
     * @return Random PolicyStatus enum value
     */
    private PolicyStatus randomStatus() {
        PolicyStatus[] statuses = PolicyStatus.values();
        return statuses[random.nextInt(statuses.length)];
    }
}
