package com.dailywallet.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OverdraftApplicationRequest {
    
    @NotNull(message = "Requested amount is required")
    @DecimalMin(value = "100.0", inclusive = false, message = "Requested amount must be at least 100")
    private BigDecimal requestedAmount;
    
    @NotBlank(message = "Purpose is required")
    private String purpose;
    
    private String repaymentPlan;
    
    private Integer repaymentDays;
    
    private Boolean acceptTerms = false;
    
    private String incomeSource;
    
    private BigDecimal monthlyIncome;
    
    private String employmentStatus;
    
    private String reasonForOverdraft;
}
