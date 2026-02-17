package com.dailywallet.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_verifications", indexes = {
    @Index(name = "idx_phone_created", columnList = "phoneNumber, createdAt")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpVerification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 15)
    private String phoneNumber;
    
    @Column(nullable = false, length = 10)
    private String otpCode;
    
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean verified = false;
    
    @Column
    private LocalDateTime verifiedAt;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer attempts = 0;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
