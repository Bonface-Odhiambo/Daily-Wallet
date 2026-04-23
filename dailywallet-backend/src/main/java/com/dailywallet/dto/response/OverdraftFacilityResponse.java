package com.dailywallet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class OverdraftFacilityResponse {
    
    private Long id;
    
    private Boolean isActive;
    
    private BigDecimal approvedLimit;
    
    private BigDecimal availableLimit;
    
    private BigDecimal utilizedAmount;
    
    private BigDecimal interestRate;
    
    private Integer disciplineScore;
    
    private String status;
    
    private LocalDateTime approvedAt;
    
    private LocalDateTime expiresAt;
    
    private BigDecimal nextPaymentDue;
    
    private LocalDateTime nextPaymentDate;
    
    private String repaymentPlan;
    
    private BigDecimal monthlyPayment;
    
    private Boolean isBehaviorLinked;
    
    private BigDecimal discountRate;
    
    private String pricingTier;
    
    private Map<String, Object> terms;
    
    private Boolean autoRepaymentEnabled;
    
    private String emergencyAccessLevel;
    
    private BigDecimal utilizationRate;
    
    private LocalDateTime lastUsedAt;
    
    private String currency = "KES";
}
