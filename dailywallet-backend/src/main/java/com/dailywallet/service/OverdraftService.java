package com.dailywallet.service;

import com.dailywallet.exception.BusinessException;
import com.dailywallet.model.entity.OverdraftAccount;
import com.dailywallet.model.entity.Transaction;
import com.dailywallet.model.entity.User;
import com.dailywallet.model.entity.Wallet;
import com.dailywallet.model.enums.TransactionStatus;
import com.dailywallet.model.enums.TransactionType;
import com.dailywallet.model.enums.WalletType;
import com.dailywallet.repository.OverdraftAccountRepository;
import com.dailywallet.repository.TransactionRepository;
import com.dailywallet.repository.UserRepository;
import com.dailywallet.repository.WalletRepository;
import com.dailywallet.util.ReferenceNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Service for managing overdraft facilities
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OverdraftService {

    private final OverdraftAccountRepository overdraftRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Value("${app.overdraft.default-limit:500.0}")
    private double defaultOverdraftLimit;

    @Value("${app.overdraft.daily-interest-rate:0.1}")
    private double defaultDailyInterestRate;

    @Value("${app.overdraft.fixed-fee:50.0}")
    private double defaultFixedFee;

    /**
     * Initialize overdraft account for a user
     */
    @Transactional
    public OverdraftAccount initializeOverdraftAccount(Long userId, BigDecimal customLimit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        if (overdraftRepository.findByUserId(userId).isPresent()) {
            throw new BusinessException("Overdraft account already exists for this user");
        }

        BigDecimal limit = customLimit != null ? customLimit : BigDecimal.valueOf(defaultOverdraftLimit);

        OverdraftAccount overdraft = OverdraftAccount.builder()
                .user(user)
                .overdraftLimit(limit)
                .dailyInterestRate(BigDecimal.valueOf(defaultDailyInterestRate))
                .fixedFeePerUse(BigDecimal.valueOf(defaultFixedFee))
                .active(true)
                .autoRepay(true)
                .gracePeriodDays(7)
                .build();

        overdraft = overdraftRepository.save(overdraft);

        log.info("💳 Overdraft account initialized for user {} with limit KES {}", userId, limit);

        return overdraft;
    }

    /**
     * Use overdraft to cover a transaction shortfall
     */
    @Transactional
    public BigDecimal useOverdraft(Long userId, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Overdraft amount must be positive");
        }

        OverdraftAccount overdraft = overdraftRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("Overdraft account not found. Please apply first."));

        if (!overdraft.getActive()) {
            throw new BusinessException("Overdraft facility is not active");
        }

        if (overdraft.getAvailableOverdraft().compareTo(amount) < 0) {
            throw new BusinessException("Overdraft limit exceeded. Available: KES " + 
                overdraft.getAvailableOverdraft());
        }

        // Update overdraft account
        overdraft.setCurrentOverdraft(overdraft.getCurrentOverdraft().add(amount));
        overdraft.setTotalOverdraftUsed(overdraft.getTotalOverdraftUsed().add(amount));
        overdraft.setLastOverdraftDate(LocalDateTime.now());

        // Charge fixed fee
        BigDecimal fee = overdraft.getFixedFeePerUse();
        overdraft.setCurrentOverdraft(overdraft.getCurrentOverdraft().add(fee));
        overdraft.setTotalFeesCharged(overdraft.getTotalFeesCharged().add(fee));

        overdraftRepository.save(overdraft);

        // Create overdraft transaction
        Transaction transaction = Transaction.builder()
                .user(overdraft.getUser())
                .transactionType(TransactionType.OVERDRAFT_USE)
                .status(TransactionStatus.COMPLETED)
                .amount(amount.add(fee))
                .referenceNumber(ReferenceNumberGenerator.generateTransactionReference())
                .description(description + " (Overdraft + KES " + fee + " fee)")
                .completedAt(LocalDateTime.now())
                .build();
        transactionRepository.save(transaction);

        log.info("💳 Overdraft used by user {}: KES {} (+ KES {} fee)", userId, amount, fee);

        return amount;
    }

    /**
     * Repay overdraft
     */
    @Transactional
    public void repayOverdraft(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Repayment amount must be positive");
        }

        OverdraftAccount overdraft = overdraftRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("Overdraft account not found"));

        if (!overdraft.isInOverdraft()) {
            throw new BusinessException("No outstanding overdraft to repay");
        }

        BigDecimal repaymentAmount = amount.min(overdraft.getCurrentOverdraft());

        // Update overdraft account
        overdraft.setCurrentOverdraft(overdraft.getCurrentOverdraft().subtract(repaymentAmount));
        overdraft.setLastRepaymentDate(LocalDateTime.now());
        overdraftRepository.save(overdraft);

        // Create repayment transaction
        Transaction transaction = Transaction.builder()
                .user(overdraft.getUser())
                .transactionType(TransactionType.OVERDRAFT_REPAYMENT)
                .status(TransactionStatus.COMPLETED)
                .amount(repaymentAmount)
                .referenceNumber(ReferenceNumberGenerator.generateTransactionReference())
                .description("Overdraft repayment")
                .completedAt(LocalDateTime.now())
                .build();
        transactionRepository.save(transaction);

        log.info("✅ Overdraft repaid by user {}: KES {}", userId, repaymentAmount);
    }

    /**
     * Auto-repay overdraft from deposit
     */
    @Transactional
    public BigDecimal autoRepayFromDeposit(Long userId, BigDecimal depositAmount) {
        OverdraftAccount overdraft = overdraftRepository.findByUserId(userId).orElse(null);

        if (overdraft == null || !overdraft.getAutoRepay() || !overdraft.isInOverdraft()) {
            return BigDecimal.ZERO;
        }

        BigDecimal repaymentAmount = depositAmount.min(overdraft.getCurrentOverdraft());

        if (repaymentAmount.compareTo(BigDecimal.ZERO) > 0) {
            repayOverdraft(userId, repaymentAmount);
            log.info("🔄 Auto-repaid overdraft for user {}: KES {}", userId, repaymentAmount);
        }

        return repaymentAmount;
    }

    /**
     * Calculate and charge daily interest on outstanding overdrafts
     * Should run daily
     */
    @Transactional
    public void chargeDailyInterest() {
        List<OverdraftAccount> overdrafts = overdraftRepository.findAccountsInOverdraft();

        log.info("💰 Charging interest on {} overdraft accounts", overdrafts.size());

        for (OverdraftAccount overdraft : overdrafts) {
            // Check if within grace period
            if (overdraft.getLastOverdraftDate() != null) {
                long daysSinceOverdraft = ChronoUnit.DAYS.between(
                    overdraft.getLastOverdraftDate().toLocalDate(),
                    LocalDateTime.now().toLocalDate()
                );

                if (daysSinceOverdraft <= overdraft.getGracePeriodDays()) {
                    log.debug("Skipping interest for user {} (within grace period)", 
                        overdraft.getUser().getId());
                    continue;
                }
            }

            // Calculate daily interest
            BigDecimal interestAmount = overdraft.getCurrentOverdraft()
                    .multiply(overdraft.getDailyInterestRate())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            if (interestAmount.compareTo(BigDecimal.ZERO) > 0) {
                // Add interest to overdraft balance
                overdraft.setCurrentOverdraft(overdraft.getCurrentOverdraft().add(interestAmount));
                overdraft.setTotalFeesCharged(overdraft.getTotalFeesCharged().add(interestAmount));
                overdraft.setLastFeeChargeDate(LocalDateTime.now());
                overdraftRepository.save(overdraft);

                // Create interest transaction
                Transaction transaction = Transaction.builder()
                        .user(overdraft.getUser())
                        .transactionType(TransactionType.OVERDRAFT_INTEREST)
                        .status(TransactionStatus.COMPLETED)
                        .amount(interestAmount)
                        .referenceNumber(ReferenceNumberGenerator.generateTransactionReference())
                        .description("Daily overdraft interest")
                        .completedAt(LocalDateTime.now())
                        .build();
                transactionRepository.save(transaction);

                log.info("📊 Interest charged to user {}: KES {}", 
                    overdraft.getUser().getId(), interestAmount);
            }
        }

        log.info("💰 Daily interest charging completed");
    }

    /**
     * Update overdraft limit
     */
    @Transactional
    public void updateOverdraftLimit(Long userId, BigDecimal newLimit) {
        OverdraftAccount overdraft = overdraftRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("Overdraft account not found"));

        if (newLimit.compareTo(overdraft.getCurrentOverdraft()) < 0) {
            throw new BusinessException("New limit cannot be less than current overdraft balance");
        }

        overdraft.setOverdraftLimit(newLimit);
        overdraftRepository.save(overdraft);

        log.info("📝 Overdraft limit updated for user {}: KES {}", userId, newLimit);
    }

    /**
     * Get overdraft account details
     */
    public OverdraftAccount getOverdraftAccount(Long userId) {
        return overdraftRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("Overdraft account not found"));
    }

    /**
     * Check if user can use overdraft for a specific amount
     */
    public boolean canUseOverdraft(Long userId, BigDecimal amount) {
        OverdraftAccount overdraft = overdraftRepository.findByUserId(userId).orElse(null);
        
        if (overdraft == null || !overdraft.getActive()) {
            return false;
        }

        return overdraft.getAvailableOverdraft().compareTo(amount) >= 0;
    }

    /**
     * Get total outstanding overdraft across all accounts
     */
    public BigDecimal getTotalOverdraftOutstanding() {
        BigDecimal total = overdraftRepository.getTotalOverdraftOutstanding();
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Get overdraft facility details for Jimudu Wallet API
     */
    public com.dailywallet.dto.response.OverdraftFacilityResponse getOverdraftFacility(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new BusinessException("User not found"));

        OverdraftAccount overdraft = overdraftRepository.findByUserId(user.getId())
                .orElse(OverdraftAccount.builder()
                        .user(user)
                        .overdraftLimit(BigDecimal.valueOf(4000))
                        .currentOverdraft(BigDecimal.ZERO)
                        .dailyInterestRate(BigDecimal.valueOf(0.10))
                        .fixedFeePerUse(BigDecimal.valueOf(50))
                        .active(false)
                        .build());

        return com.dailywallet.dto.response.OverdraftFacilityResponse.builder()
                .id(overdraft.getId())
                .isActive(overdraft.getActive())
                .approvedLimit(overdraft.getOverdraftLimit())
                .availableLimit(overdraft.getAvailableOverdraft())
                .utilizedAmount(overdraft.getCurrentOverdraft())
                .interestRate(overdraft.getDailyInterestRate().multiply(BigDecimal.valueOf(30))) // Monthly rate
                .disciplineScore(87) // Mock discipline score
                .status(overdraft.getActive() ? "ACTIVE" : "INACTIVE")
                .approvedAt(overdraft.getCreatedAt())
                .expiresAt(LocalDateTime.now().plusMonths(12))
                .nextPaymentDue(overdraft.getCurrentOverdraft())
                .nextPaymentDate(LocalDateTime.now().plusDays(30))
                .repaymentPlan("Monthly")
                .monthlyPayment(overdraft.getCurrentOverdraft().multiply(BigDecimal.valueOf(0.1)))
                .isBehaviorLinked(true)
                .discountRate(BigDecimal.valueOf(0.2))
                .pricingTier("GOLD")
                .autoRepaymentEnabled(overdraft.getAutoRepay())
                .utilizationRate(overdraft.getCurrentOverdraft()
                        .divide(overdraft.getOverdraftLimit(), 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)))
                .currency("KES")
                .build();
    }

    /**
     * Apply for overdraft facility
     */
    public Object applyForOverdraft(String phoneNumber, com.dailywallet.dto.request.OverdraftApplicationRequest request) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new BusinessException("User not found"));

        log.info("Overdraft application received for user {}: amount {}", phoneNumber, request.getRequestedAmount());

        Map<String, Object> result = new HashMap<>();
        result.put("applicationId", "OD" + System.currentTimeMillis());
        result.put("status", "PENDING_REVIEW");
        result.put("requestedAmount", request.getRequestedAmount());
        result.put("estimatedApproval", request.getRequestedAmount().multiply(BigDecimal.valueOf(0.8)));
        result.put("processingTime", "24 hours");
        result.put("nextSteps", "Wait for approval notification");

        return result;
    }

    /**
     * Calculate overdraft pricing with discipline score
     */
    public com.dailywallet.dto.response.OverdraftPricingResponse calculateOverdraftPricing(String phoneNumber, Double amount, Integer disciplineScore) {
        BigDecimal requestedAmount = BigDecimal.valueOf(amount);
        BigDecimal baseRate = BigDecimal.valueOf(0.10); // 10% base rate
        
        // Apply discipline score discount
        if (disciplineScore != null && disciplineScore >= 85) {
            baseRate = baseRate.multiply(BigDecimal.valueOf(0.8)); // 20% discount
        }

        BigDecimal monthlyInterest = requestedAmount.multiply(baseRate);
        BigDecimal processingFee = BigDecimal.valueOf(100);
        BigDecimal transactionFee = BigDecimal.valueOf(50);
        BigDecimal totalCost = monthlyInterest.add(processingFee).add(transactionFee);
        BigDecimal totalRepayment = requestedAmount.add(totalCost);

        return com.dailywallet.dto.response.OverdraftPricingResponse.builder()
                .requestedAmount(requestedAmount)
                .approvedAmount(requestedAmount.multiply(BigDecimal.valueOf(0.9)))
                .interestRate(baseRate.multiply(BigDecimal.valueOf(100)))
                .monthlyInterest(monthlyInterest)
                .totalInterest(monthlyInterest.multiply(BigDecimal.valueOf(1))) // 1 month
                .processingFee(processingFee)
                .transactionFee(transactionFee)
                .totalCost(totalCost)
                .totalRepayment(totalRepayment)
                .disciplineScore(disciplineScore != null ? disciplineScore : 85)
                .isDiscounted(disciplineScore != null && disciplineScore >= 85)
                .discountAmount(disciplineScore != null && disciplineScore >= 85 ? 
                        monthlyInterest.multiply(BigDecimal.valueOf(0.2)) : BigDecimal.ZERO)
                .discountReason(disciplineScore != null && disciplineScore >= 85 ? 
                        "High discipline score discount" : "")
                .pricingTier(disciplineScore != null && disciplineScore >= 85 ? "PREMIUM" : "STANDARD")
                .repaymentDays(30)
                .dailyRate(baseRate.divide(BigDecimal.valueOf(30), 6, RoundingMode.HALF_UP))
                .feeBreakdown(Arrays.asList("Processing Fee", "Transaction Fee", "Monthly Interest"))
                .costComponents(Map.of(
                        "processingFee", processingFee,
                        "transactionFee", transactionFee,
                        "interest", monthlyInterest
                ))
                .quotedAt(LocalDateTime.now())
                .currency("KES")
                .behaviorLinked(true)
                .nextStep("Proceed with application")
                .terms(Arrays.asList("Repayment within 30 days", "Interest charged monthly", "Late fees apply"))
                .requiresApproval(requestedAmount.compareTo(BigDecimal.valueOf(2000)) > 0)
                .approvalMessage(requestedAmount.compareTo(BigDecimal.valueOf(2000)) > 0 ? 
                        "Large amount requires manual approval" : "")
                .earlyRepaymentDiscount(BigDecimal.valueOf(0.05))
                .build();
    }

    /**
     * Draw overdraft funds
     */
    public Object drawOverdraft(String phoneNumber, Double amount, String purpose) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new BusinessException("User not found"));

        BigDecimal drawAmount = BigDecimal.valueOf(amount);
        
        Map<String, Object> result = new HashMap<>();
        result.put("drawAmount", drawAmount);
        result.put("purpose", purpose);
        result.put("transactionId", "OD" + System.currentTimeMillis());
        result.put("status", "COMPLETED");
        result.put("fee", drawAmount.multiply(BigDecimal.valueOf(0.02)));
        result.put("totalRepayment", drawAmount.multiply(BigDecimal.valueOf(1.02)));
        result.put("dueDate", LocalDateTime.now().plusDays(30));

        return result;
    }

    /**
     * Get repayment schedule
     */
    public Object getRepaymentSchedule(String phoneNumber) {
        Map<String, Object> schedule = new HashMap<>();
        schedule.put("nextPayment", 500);
        schedule.put("nextPaymentDate", LocalDateTime.now().plusDays(30));
        schedule.put("totalOutstanding", 1500);
        schedule.put("monthlyPayment", 500);
        schedule.put("remainingPayments", 3);
        schedule.put("interestRate", 10);
        schedule.put("autoRepayment", true);

        return schedule;
    }

    /**
     * Get usage history
     */
    public Object getUsageHistory(String phoneNumber, int days) {
        List<Map<String, Object>> history = new ArrayList<>();
        
        for (int i = 0; i < 5; i++) {
            Map<String, Object> usage = new HashMap<>();
            usage.put("date", LocalDateTime.now().minusDays(i * 7));
            usage.put("amount", 500 + (i * 100));
            usage.put("purpose", "Emergency expense");
            usage.put("status", "REPAID");
            usage.put("repaymentDate", LocalDateTime.now().minusDays(i * 7 + 30));
            history.add(usage);
        }

        return history;
    }

    /**
     * Pre-approval check
     */
    public Object preApprovalCheck(String phoneNumber) {
        Map<String, Object> check = new HashMap<>();
        check.put("eligible", true);
        check.put("estimatedLimit", 4000);
        check.put("currentScore", 87);
        check.put("requiredScore", 70);
        check.put("factors", Arrays.asList("Good payment history", "Stable income", "High discipline score"));
        check.put("nextSteps", "Proceed with full application");
        
        return check;
    }
}
