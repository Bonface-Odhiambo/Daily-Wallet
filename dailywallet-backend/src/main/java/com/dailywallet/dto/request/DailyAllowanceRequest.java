package com.dailywallet.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DailyAllowanceRequest {
    
    @NotNull(message = "Daily amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Daily amount must be greater than 0")
    private BigDecimal dailyAmount;
    
    @NotNull(message = "Emergency savings target is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Emergency savings target must be greater than 0")
    private BigDecimal emergencySavingsTarget;
    
    private Boolean mmfSweepEnabled = false;
    
    private BigDecimal weeklyAllowance;
    
    private BigDecimal monthlyAllowance;
    
    private String timezone = "Africa/Nairobi";
    
    private Boolean weekendAccess = false;
    
    private BigDecimal emergencyFundPercentage = BigDecimal.valueOf(20);
}
