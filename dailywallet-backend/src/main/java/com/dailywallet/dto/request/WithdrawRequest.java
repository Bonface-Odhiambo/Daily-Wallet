package com.dailywallet.dto.request;

import com.dailywallet.model.enums.WalletType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawRequest {
    
    @NotNull(message = "Wallet type is required")
    private WalletType walletType;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Minimum withdrawal amount is KES 1")
    private BigDecimal amount;
    
    @NotBlank(message = "Recipient phone number is required")
    @Pattern(regexp = "^254[0-9]{9}$", message = "Phone number must be in format 254XXXXXXXXX")
    private String recipientPhoneNumber;
}
