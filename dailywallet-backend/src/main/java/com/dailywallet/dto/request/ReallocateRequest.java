package com.dailywallet.dto.request;

import com.dailywallet.model.enums.WalletType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReallocateRequest {
    
    @NotNull(message = "Source wallet type is required")
    private WalletType fromWalletType;
    
    @NotNull(message = "Destination wallet type is required")
    private WalletType toWalletType;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Minimum reallocation amount is KES 1")
    private BigDecimal amount;
}
