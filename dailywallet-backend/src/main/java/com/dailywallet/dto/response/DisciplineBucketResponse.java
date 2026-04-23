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
public class DisciplineBucketResponse {
    
    private Long id;
    
    private String bucketType;
    
    private String bucketName;
    
    private BigDecimal balance;
    
    private BigDecimal availableBalance;
    
    private BigDecimal lockedBalance;
    
    private BigDecimal totalDeposited;
    
    private BigDecimal totalWithdrawn;
    
    private BigDecimal totalInterestEarned;
    
    private BigDecimal dailyAllowanceAmount;
    
    private BigDecimal emergencySavingsTarget;
    
    private Boolean mmfSweepEnabled;
    
    private LocalDateTime lastReleaseAt;
    
    private LocalDateTime nextReleaseAt;
    
    private BigDecimal progressPercentage;
    
    private String status;
    
    private Map<String, Object> metadata;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private BigDecimal dailySpendingLimit;
    
    private BigDecimal remainingDailyAllowance;
    
    private Boolean isProtected;
    
    private String description;
}
