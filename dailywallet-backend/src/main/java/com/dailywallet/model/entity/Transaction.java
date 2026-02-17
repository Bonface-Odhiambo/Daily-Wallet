package com.dailywallet.model.entity;

import com.dailywallet.model.enums.TransactionCategory;
import com.dailywallet.model.enums.TransactionStatus;
import com.dailywallet.model.enums.TransactionType;
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
@Table(name = "transactions", indexes = {
    @Index(name = "idx_user_created", columnList = "user_id, createdAt"),
    @Index(name = "idx_reference", columnList = "referenceNumber", unique = true),
    @Index(name = "idx_mpesa_ref", columnList = "mpesaReceiptNumber")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_wallet_id")
    private Wallet fromWallet;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_wallet_id")
    private Wallet toWallet;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TransactionType transactionType;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private TransactionCategory category;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Column(precision = 19, scale = 2)
    private BigDecimal fee;
    
    @Column(nullable = false, unique = true, length = 50)
    private String referenceNumber;
    
    @Column(length = 50)
    private String mpesaReceiptNumber;
    
    @Column(length = 15)
    private String recipientPhoneNumber;
    
    @Column(length = 500)
    private String description;
    
    @Column(length = 1000)
    private String metadata;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column
    private LocalDateTime completedAt;
}
