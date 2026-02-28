package com.dailywallet.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents an overdraft facility for a user.
 * Allows users to spend beyond their available balance up to a limit.
 */
@Entity
@Table(name = "overdraft_accounts", indexes = {
    @Index(name = "idx_user_overdraft", columnList = "user_id", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OverdraftAccount {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal overdraftLimit = BigDecimal.ZERO; // Maximum overdraft allowed
    
    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal currentOverdraft = BigDecimal.ZERO; // Current overdraft balance (amount owed)
    
    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal totalOverdraftUsed = BigDecimal.ZERO; // Lifetime overdraft usage
    
    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal totalFeesCharged = BigDecimal.ZERO; // Total overdraft fees charged
    
    @Column(nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal dailyInterestRate = BigDecimal.valueOf(0.1); // Daily interest rate (0.1% = 36.5% annual)
    
    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal fixedFeePerUse = BigDecimal.valueOf(50.0); // Fixed fee per overdraft usage (KES 50)
    
    @Column
    private LocalDateTime lastOverdraftDate; // Last time overdraft was used
    
    @Column
    private LocalDateTime lastRepaymentDate; // Last time overdraft was repaid
    
    @Column
    private LocalDateTime lastFeeChargeDate; // Last time fee was charged
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true; // Whether overdraft facility is active
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean autoRepay = true; // Auto-repay from next deposit
    
    @Column(nullable = false)
    @Builder.Default
    private Integer gracePeriodDays = 7; // Days before interest starts accruing
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Calculate available overdraft (limit - current usage)
     */
    public BigDecimal getAvailableOverdraft() {
        return overdraftLimit.subtract(currentOverdraft);
    }
    
    /**
     * Check if user is in overdraft
     */
    public boolean isInOverdraft() {
        return currentOverdraft.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * Calculate utilization percentage
     */
    public BigDecimal getUtilizationPercentage() {
        if (overdraftLimit.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentOverdraft.divide(overdraftLimit, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
