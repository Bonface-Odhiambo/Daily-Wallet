package com.dailywallet.service;

import com.dailywallet.dto.request.FeeBreakdownRequest;
import com.dailywallet.dto.response.FeeBreakdownResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransparencyService {

    public FeeBreakdownResponse calculateFeeBreakdown(String phoneNumber, FeeBreakdownRequest request) {
        log.info("Calculating fee breakdown for user {}: {} {}", phoneNumber, request.getTransactionType(), request.getPrincipalAmount());

        BigDecimal principalAmount = request.getPrincipalAmount();
        String transactionType = request.getTransactionType();
        Integer disciplineScore = request.getDisciplineScore() != null ? request.getDisciplineScore() : 85;

        // Calculate fees based on transaction type and discipline score
        BigDecimal managementFee = calculateManagementFee(transactionType, principalAmount, disciplineScore);
        BigDecimal transactionFee = calculateTransactionFee(transactionType, principalAmount);
        BigDecimal overdraftCharge = calculateOverdraftCharge(transactionType, principalAmount, disciplineScore);
        BigDecimal interestAmount = calculateInterest(transactionType, principalAmount, request.getDuration());

        BigDecimal totalCharge = managementFee.add(transactionFee).add(overdraftCharge).add(interestAmount);
        BigDecimal totalAmount = principalAmount.add(totalCharge);

        // Apply discipline score discount
        BigDecimal discountApplied = BigDecimal.ZERO;
        String discountReason = "";
        if (disciplineScore >= 85) {
            discountApplied = totalCharge.multiply(BigDecimal.valueOf(0.1)); // 10% discount
            totalCharge = totalCharge.subtract(discountApplied);
            totalAmount = principalAmount.add(totalCharge);
            discountReason = "High discipline score (85+) - 10% discount applied";
        }

        return FeeBreakdownResponse.builder()
                .principalAmount(principalAmount)
                .managementFee(managementFee)
                .transactionFee(transactionFee)
                .overdraftCharge(overdraftCharge)
                .interestAmount(interestAmount)
                .totalCharge(totalCharge)
                .totalAmount(totalAmount)
                .transactionType(transactionType)
                .disciplineScore(disciplineScore)
                .discountApplied(discountApplied)
                .discountReason(discountReason)
                .feeComponents(Arrays.asList("Management Fee", "Transaction Fee", "Overdraft Charge", "Interest"))
                .feeDetails(createFeeDetails(managementFee, transactionFee, overdraftCharge, interestAmount))
                .calculatedAt(LocalDateTime.now())
                .currency("KES")
                .transparentPricing(true)
                .pricingTier(calculatePricingTier(disciplineScore))
                .disclaimers(getDisclaimers(transactionType))
                .nextStepConfirmation("Please confirm to proceed with the transaction")
                .requiresApproval(totalAmount.compareTo(BigDecimal.valueOf(5000)) > 0)
                .approvalMessage(totalAmount.compareTo(BigDecimal.valueOf(5000)) > 0 ? "Large transaction requires approval" : "")
                .build();
    }

    public Object getTransactionCostBreakdown(String phoneNumber, String transactionId) {
        Map<String, Object> breakdown = new HashMap<>();
        breakdown.put("transactionId", transactionId);
        breakdown.put("totalCost", 1250);
        breakdown.put("feeBreakdown", Map.of(
            "principal", 1000,
            "managementFee", 150,
            "transactionFee", 50,
            "interest", 50
        ));
        breakdown.put("timestamp", LocalDateTime.now());
        breakdown.put("transparent", true);
        return breakdown;
    }

    public Object getPricingModel(String phoneNumber) {
        Map<String, Object> pricingModel = new HashMap<>();
        pricingModel.put("baseManagementFee", "2%");
        pricingModel.put("transactionFee", "KES 50");
        pricingModel.put("overdraftRate", "10% monthly");
        pricingModel.put("mmfYield", "1.2% monthly");
        pricingModel.put("disciplineDiscount", "Up to 15%");
        pricingModel.put("transparent", true);
        pricingModel.put("noHiddenFees", true);
        return pricingModel;
    }

    public Object getFeeHistory(String phoneNumber, int days) {
        List<Map<String, Object>> history = new ArrayList<>();
        
        // Mock fee history data
        for (int i = 0; i < 10; i++) {
            Map<String, Object> fee = new HashMap<>();
            fee.put("date", LocalDateTime.now().minusDays(i));
            fee.put("transactionType", "DEPOSIT");
            fee.put("amount", 1000 + (i * 100));
            fee.put("totalFee", 50 + (i * 5));
            fee.put("feeBreakdown", Map.of(
                "managementFee", 30 + (i * 3),
                "transactionFee", 20 + (i * 2)
            ));
            fee.put("disciplineScore", 85 + (i % 10));
            fee.put("discountApplied", i % 3 == 0 ? 5 : 0);
            history.add(fee);
        }
        
        return history;
    }

    private BigDecimal calculateManagementFee(String transactionType, BigDecimal amount, Integer disciplineScore) {
        BigDecimal baseRate = BigDecimal.valueOf(0.02); // 2% base rate
        
        // Apply discipline score discount
        if (disciplineScore >= 90) {
            baseRate = baseRate.multiply(BigDecimal.valueOf(0.85)); // 15% discount
        } else if (disciplineScore >= 80) {
            baseRate = baseRate.multiply(BigDecimal.valueOf(0.9)); // 10% discount
        } else if (disciplineScore >= 70) {
            baseRate = baseRate.multiply(BigDecimal.valueOf(0.95)); // 5% discount
        }
        
        return amount.multiply(baseRate).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTransactionFee(String transactionType, BigDecimal amount) {
        // Fixed transaction fee based on type
        switch (transactionType.toUpperCase()) {
            case "DEPOSIT":
                return BigDecimal.valueOf(30);
            case "WITHDRAWAL":
                return BigDecimal.valueOf(50);
            case "OVERDRAFT":
                return BigDecimal.valueOf(100);
            default:
                return BigDecimal.valueOf(40);
        }
    }

    private BigDecimal calculateOverdraftCharge(String transactionType, BigDecimal amount, Integer disciplineScore) {
        if (!"OVERDRAFT".equalsIgnoreCase(transactionType)) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal baseRate = BigDecimal.valueOf(0.10); // 10% monthly base rate
        
        // Apply discipline score discount
        if (disciplineScore >= 85) {
            baseRate = baseRate.multiply(BigDecimal.valueOf(0.8)); // 20% discount for high discipline
        }
        
        return amount.multiply(baseRate).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateInterest(String transactionType, BigDecimal amount, String duration) {
        if (!"OVERDRAFT".equalsIgnoreCase(transactionType) || duration == null) {
            return BigDecimal.ZERO;
        }
        
        // Simple interest calculation for overdraft
        BigDecimal monthlyRate = BigDecimal.valueOf(0.08); // 8% monthly rate
        return amount.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
    }

    private Map<String, BigDecimal> createFeeDetails(BigDecimal managementFee, BigDecimal transactionFee, BigDecimal overdraftCharge, BigDecimal interest) {
        Map<String, BigDecimal> details = new HashMap<>();
        details.put("managementFee", managementFee);
        details.put("transactionFee", transactionFee);
        details.put("overdraftCharge", overdraftCharge);
        details.put("interest", interest);
        return details;
    }

    private String calculatePricingTier(Integer disciplineScore) {
        if (disciplineScore >= 90) return "PREMIUM";
        if (disciplineScore >= 80) return "GOLD";
        if (disciplineScore >= 70) return "SILVER";
        return "STANDARD";
    }

    private List<String> getDisclaimers(String transactionType) {
        List<String> disclaimers = new ArrayList<>();
        disclaimers.add("All fees are displayed upfront with no hidden charges");
        disclaimers.add("Discipline score discounts apply automatically");
        
        if ("OVERDRAFT".equalsIgnoreCase(transactionType)) {
            disclaimers.add("Overdraft interest is calculated monthly");
            disclaimers.add("Late repayment may affect future borrowing limits");
        }
        
        return disclaimers;
    }
}
