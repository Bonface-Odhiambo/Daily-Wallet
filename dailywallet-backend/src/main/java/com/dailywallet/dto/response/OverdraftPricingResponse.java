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
public class OverdraftPricingResponse {
    
    private BigDecimal requestedAmount;
    
    private BigDecimal approvedAmount;
    
    private BigDecimal interestRate;
    
    private BigDecimal monthlyInterest;
    
    private BigDecimal totalInterest;
    
    private BigDecimal processingFee;
    
    private BigDecimal transactionFee;
    
    private BigDecimal totalCost;
    
    private BigDecimal totalRepayment;
    
    private Integer disciplineScore;
    
    private Boolean isDiscounted;
    
    private BigDecimal discountAmount;
    
    private String discountReason;
    
    private String pricingTier;
    
    private Integer repaymentDays;
    
    private BigDecimal dailyRate;
    
    private List<String> feeBreakdown;
    
    private Map<String, BigDecimal> costComponents;
    
    private LocalDateTime quotedAt;
    
    private String currency = "KES";
    
    private Boolean behaviorLinked;
    
    private String nextStep;
    
    private List<String> terms;
    
    private Boolean requiresApproval;
    
    private String approvalMessage;
    
    private BigDecimal earlyRepaymentDiscount;
}
