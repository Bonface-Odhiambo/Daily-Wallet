package com.dailywallet.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "interest_records", indexes = {
    @Index(name = "idx_wallet_date", columnList = "wallet_id, calculationDate")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterestRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;
    
    @Column(nullable = false)
    private LocalDate calculationDate;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal principalAmount;
    
    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal interestAmount;
    
    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal dailyRate;
    
    @OneToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
