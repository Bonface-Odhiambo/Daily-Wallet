package com.dailywallet.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FeeBreakdownRequest {
    
    @NotBlank(message = "Transaction type is required")
    private String transactionType;
    
    @NotNull(message = "Principal amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Principal amount must be greater than 0")
    private BigDecimal principalAmount;
    
    private String duration;
    
    private Integer disciplineScore;
    
    private String bucketType;
    
    private String paymentMethod;
    
    private Boolean urgent = false;
    
    private String purpose;
}
