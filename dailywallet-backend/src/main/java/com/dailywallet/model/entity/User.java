package com.dailywallet.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_phone_number", columnList = "phoneNumber", unique = true),
    @Index(name = "idx_email", columnList = "email", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 15)
    private String phoneNumber;
    
    @Column(unique = true, length = 100)
    private String email;
    
    @Column(nullable = false, length = 100)
    private String fullName;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private Boolean phoneVerified = false;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @Column(nullable = false)
    private Boolean onboardingCompleted = false;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Wallet> wallets = new ArrayList<>();
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private AllocationRule allocationRule;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column
    private LocalDateTime lastLoginAt;
}
