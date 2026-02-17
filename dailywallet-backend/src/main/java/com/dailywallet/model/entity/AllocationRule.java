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

@Entity
@Table(name = "allocation_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllocationRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal dailyPercentage;
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal weeklyPercentage;
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal monthlyPercentage;
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal savingsPercentage;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    @PreUpdate
    private void validatePercentages() {
        BigDecimal total = dailyPercentage
            .add(weeklyPercentage)
            .add(monthlyPercentage)
            .add(savingsPercentage);
        
        if (total.compareTo(new BigDecimal("100")) != 0) {
            throw new IllegalStateException("Allocation percentages must sum to 100");
        }
    }
}
