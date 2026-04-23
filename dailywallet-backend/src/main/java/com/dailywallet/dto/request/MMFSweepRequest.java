package com.dailywallet.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MMFSweepRequest {
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Amount must be non-negative")
    private BigDecimal amount;
    
    private Boolean autoSweep = false;
    
    private String sweepFrequency = "daily";
    
    private BigDecimal minimumSweepAmount = BigDecimal.valueOf(100);
    
    private String sweepTime = "00:00";
    
    private Boolean emergencyFundProtection = true;
    
    private BigDecimal emergencyFundThreshold = BigDecimal.valueOf(1000);
    
    private String mmfProvider = "default";
    
    private Boolean compoundInterest = true;
}
