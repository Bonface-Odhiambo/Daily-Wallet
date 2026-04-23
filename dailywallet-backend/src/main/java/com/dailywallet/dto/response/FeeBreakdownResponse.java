package com.dailywallet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class FeeBreakdownResponse {
    
    private BigDecimal principalAmount;
    
    private BigDecimal managementFee;
    
    private BigDecimal transactionFee;
    
    private BigDecimal overdraftCharge;
    
    private BigDecimal interestAmount;
    
    private BigDecimal totalCharge;
    
    private BigDecimal totalAmount;
    
    private String transactionType;
    
    private Integer disciplineScore;
    
    private BigDecimal discountApplied;
    
    private String discountReason;
    
    private List<String> feeComponents;
    
    private Map<String, BigDecimal> feeDetails;
    
    private LocalDateTime calculatedAt;
    
    private String currency = "KES";
    
    private Boolean transparentPricing;
    
    private String pricingTier;
    
    private List<String> disclaimers;
    
    private String nextStepConfirmation;
    
    private Boolean requiresApproval;
    
    private String approvalMessage;
}
