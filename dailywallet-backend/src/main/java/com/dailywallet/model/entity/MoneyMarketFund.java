package com.dailywallet.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a Money Market Fund investment for a user's savings wallet.
 * Tracks the actual investment in external MMF providers (e.g., CIC Money Market Fund, NCBA Money Market Fund)
 */
@Entity
@Table(name = "money_market_funds", indexes = {
    @Index(name = "idx_wallet_mmf", columnList = "wallet_id"),
    @Index(name = "idx_user_mmf", columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoneyMarketFund {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false, unique = true)
    private Wallet wallet;
    
    @Column(nullable = false, length = 100)
    private String mmfProvider; // e.g., "CIC Money Market Fund", "NCBA Money Market Fund"
    
    @Column(nullable = false, length = 50)
    private String mmfAccountNumber; // Account number with the MMF provider
    
    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal totalInvested = BigDecimal.ZERO; // Total amount invested in MMF
    
    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal currentValue = BigDecimal.ZERO; // Current NAV value
    
    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal totalReturns = BigDecimal.ZERO; // Total returns earned
    
    @Column(nullable = false, precision = 10, scale = 6)
    @Builder.Default
    private BigDecimal currentNav = BigDecimal.ONE; // Current Net Asset Value per unit
    
    @Column(nullable = false, precision = 19, scale = 6)
    @Builder.Default
    private BigDecimal totalUnits = BigDecimal.ZERO; // Total units owned
    
    @Column(nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal annualReturnRate = BigDecimal.valueOf(13.0); // Expected annual return %
    
    @Column
    private LocalDate lastNavUpdate; // Last time NAV was updated
    
    @Column
    private LocalDateTime lastInvestmentDate; // Last investment date
    
    @Column
    private LocalDateTime lastRedemptionDate; // Last redemption date
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true; // Whether MMF account is active
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Calculate current return percentage
     */
    public BigDecimal getReturnPercentage() {
        if (totalInvested.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return totalReturns.divide(totalInvested, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
    
    /**
     * Calculate unrealized gains
     */
    public BigDecimal getUnrealizedGains() {
        return currentValue.subtract(totalInvested);
    }
}
