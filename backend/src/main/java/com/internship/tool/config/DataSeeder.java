package com.internship.tool.config;

import com.internship.tool.entity.Policy;
import com.internship.tool.repository.PolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Autowired
    private PolicyRepository policyRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seedPolicies() {
        if (policyRepository.count() > 0) {
            logger.info("Database already seeded with {} policies. Skipping DataSeeder.", policyRepository.count());
            return;
        }

        logger.info("Seeding 30 realistic demo policies...");

        List<Policy> policies = List.of(
                createPolicy("AutoShield Premium", "Auto Insurance", "Active", "John Smith", LocalDate.of(2025, 6, 15)),
                createPolicy("HomeGuard Basic", "Home Insurance", "Active", "Sarah Johnson", LocalDate.of(2025, 8, 20)),
                createPolicy("LifeSecure Term 20", "Life Insurance", "Pending", "Michael Brown", LocalDate.of(2026, 3, 10)),
                createPolicy("HealthFirst Platinum", "Health Insurance", "Active", "Emily Davis", LocalDate.of(2025, 12, 1)),
                createPolicy("BizProtect SME", "Business Insurance", "Active", "Robert Wilson", LocalDate.of(2025, 9, 30)),
                createPolicy("TravelSafe Global", "Travel Insurance", "Pending", "Lisa Anderson", LocalDate.of(2025, 7, 25)),
                createPolicy("PetCare Plus", "Pet Insurance", "Active", "David Martinez", LocalDate.of(2025, 11, 18)),
                createPolicy("MotorCycle Pro", "Auto Insurance", "Active", "Jennifer Taylor", LocalDate.of(2025, 5, 5)),
                createPolicy("SeniorCare Gold", "Health Insurance", "Active", "Thomas Robinson", LocalDate.of(2025, 10, 12)),
                createPolicy("StudentShield", "Health Insurance", "Pending", "Amanda White", LocalDate.of(2025, 4, 28)),
                createPolicy("CommercialFleet Cover", "Business Insurance", "Active", "Christopher Harris", LocalDate.of(2025, 8, 8)),
                createPolicy("FamilyTerm Life", "Life Insurance", "Active", "Jessica Clark", LocalDate.of(2026, 1, 15)),
                createPolicy("RentersSafe", "Home Insurance", "Pending", "Matthew Lewis", LocalDate.of(2025, 6, 30)),
                createPolicy("AdventureTravel", "Travel Insurance", "Active", "Ashley Walker", LocalDate.of(2025, 3, 20)),
                createPolicy("ClassicCar Heritage", "Auto Insurance", "Active", "Daniel Hall", LocalDate.of(2025, 9, 15)),
                createPolicy("DisabilityIncome Guard", "Life Insurance", "Pending", "Nicole Allen", LocalDate.of(2026, 2, 28)),
                createPolicy("CondoMaster Policy", "Home Insurance", "Active", "Ryan Young", LocalDate.of(2025, 7, 10)),
                createPolicy("GroupHealth Corp", "Health Insurance", "Active", "Stephanie King", LocalDate.of(2025, 11, 5)),
                createPolicy("MarineYacht Cover", "Business Insurance", "Pending", "Jason Wright", LocalDate.of(2025, 5, 25)),
                createPolicy("CyberRisk Shield", "Business Insurance", "Active", "Melissa Lopez", LocalDate.of(2025, 12, 20)),
                createPolicy("GapCoverage Auto", "Auto Insurance", "Active", "Kevin Hill", LocalDate.of(2025, 4, 15)),
                createPolicy("CriticalIllness Care", "Health Insurance", "Pending", "Laura Scott", LocalDate.of(2025, 8, 28)),
                createPolicy("WholeLife Legacy", "Life Insurance", "Active", "Mark Green", LocalDate.of(2027, 1, 1)),
                createPolicy("Snowbird Travel", "Travel Insurance", "Active", "Rachel Adams", LocalDate.of(2025, 2, 14)),
                createPolicy("FarmersCrop Protect", "Business Insurance", "Active", "Steven Baker", LocalDate.of(2025, 10, 30)),
                createPolicy("TelematicsDrive Safe", "Auto Insurance", "Pending", "Rebecca Gonzalez", LocalDate.of(2025, 6, 5)),
                createPolicy("LongTermCare Wise", "Health Insurance", "Active", "Edward Nelson", LocalDate.of(2025, 9, 18)),
                createPolicy("EventCancellation Pro", "Travel Insurance", "Pending", "Michelle Carter", LocalDate.of(2025, 7, 22)),
                createPolicy("EmployeeBenefits Plus", "Health Insurance", "Active", "Timothy Mitchell", LocalDate.of(2025, 11, 12)),
                createPolicy("RVRoadtrip Cover", "Auto Insurance", "Active", "Angela Perez", LocalDate.of(2025, 5, 18)),
                // Additional variety for demo analytics
                createPolicy("ExpiredAuto Legacy", "Auto Insurance", "COMPLETED", "James Carter", LocalDate.of(2023, 12, 31)),
                createPolicy("LapsedHome Policy", "Home Insurance", "DELETED", "Patricia Moore", LocalDate.of(2024, 1, 15))
        );

        policyRepository.saveAll(policies);
        logger.info("Successfully seeded {} policies.", policies.size());

        long activeCount = policyRepository.countByStatusAndIsDeletedFalse("Active");
        long pendingCount = policyRepository.countByStatusAndIsDeletedFalse("Pending");
        long completedCount = policyRepository.countByStatusAndIsDeletedFalse("COMPLETED");
        long deletedCount = policyRepository.countByStatusAndIsDeletedFalse("DELETED");
        logger.info("Status breakdown — Active: {}, Pending: {}, COMPLETED: {}, DELETED: {}",
                activeCount, pendingCount, completedCount, deletedCount);
    }

    private Policy createPolicy(String name, String type, String status, String holder, LocalDate expiry) {
        Policy policy = new Policy();
        policy.setPolicyName(name);
        policy.setPolicyType(type);
        policy.setStatus(status);
        policy.setPolicyHolder(holder);
        policy.setExpiryDate(expiry);
        policy.setIsDeleted("DELETED".equals(status));
        policy.setCreatedAt(LocalDateTime.now());
        policy.setUpdatedAt(LocalDateTime.now());
        return policy;
    }
}

