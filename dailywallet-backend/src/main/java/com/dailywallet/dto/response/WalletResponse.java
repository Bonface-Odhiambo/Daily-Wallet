package com.dailywallet.dto.response;

import com.dailywallet.model.enums.WalletType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {
    private Long id;
    private WalletType walletType;
    private BigDecimal balance;
    private BigDecimal lockedBalance;
    private BigDecimal availableBalance;
    private BigDecimal totalDeposited;
    private BigDecimal totalWithdrawn;
    private BigDecimal totalInterestEarned;
    private LocalDateTime lastReleaseAt;
    private LocalDateTime nextReleaseAt;
}
