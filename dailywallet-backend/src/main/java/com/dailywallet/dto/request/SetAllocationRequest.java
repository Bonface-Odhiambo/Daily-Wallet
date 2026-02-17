package com.dailywallet.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SetAllocationRequest {
    
    @NotNull(message = "Daily percentage is required")
    @DecimalMin(value = "0.00", message = "Daily percentage must be at least 0")
    @DecimalMax(value = "100.00", message = "Daily percentage must be at most 100")
    private BigDecimal dailyPercentage;
    
    @NotNull(message = "Weekly percentage is required")
    @DecimalMin(value = "0.00", message = "Weekly percentage must be at least 0")
    @DecimalMax(value = "100.00", message = "Weekly percentage must be at most 100")
    private BigDecimal weeklyPercentage;
    
    @NotNull(message = "Monthly percentage is required")
    @DecimalMin(value = "0.00", message = "Monthly percentage must be at least 0")
    @DecimalMax(value = "100.00", message = "Monthly percentage must be at most 100")
    private BigDecimal monthlyPercentage;
    
    @NotNull(message = "Savings percentage is required")
    @DecimalMin(value = "0.00", message = "Savings percentage must be at least 0")
    @DecimalMax(value = "100.00", message = "Savings percentage must be at most 100")
    private BigDecimal savingsPercentage;
}
