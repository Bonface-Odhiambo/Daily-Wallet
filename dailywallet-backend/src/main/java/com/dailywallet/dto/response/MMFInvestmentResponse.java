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
public class MMFInvestmentResponse {
    
    private Long id;
    
    private BigDecimal investedAmount;
    
    private BigDecimal currentValue;
    
    private BigDecimal totalYield;
    
    private BigDecimal yieldRate;
    
    private BigDecimal annualizedReturn;
    
    private String mmfProvider;
    
    private String fundName;
    
    private LocalDateTime investmentDate;
    
    private LocalDateTime lastUpdated;
    
    private Boolean autoSweepEnabled;
    
    private BigDecimal sweepAmount;
    
    private String sweepFrequency;
    
    private BigDecimal managementFee;
    
    private BigDecimal netYield;
    
    private Integer daysInvested;
    
    private BigDecimal dailyYield;
    
    private Map<String, Object> performanceMetrics;
    
    private String status;
    
    private String currency = "KES";
    
    private Boolean compoundInterest;
    
    private BigDecimal targetAmount;
    
    private BigDecimal progressPercentage;
    
    private LocalDateTime maturityDate;
    
    private Boolean isProtected;
}
