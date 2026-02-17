package com.dailywallet.repository;

import com.dailywallet.model.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {
    
    Optional<OtpVerification> findTopByPhoneNumberAndVerifiedFalseOrderByCreatedAtDesc(String phoneNumber);
    
    Optional<OtpVerification> findByPhoneNumberAndOtpCode(String phoneNumber, String otpCode);
    
    void deleteByPhoneNumber(String phoneNumber);
    
    void deleteByPhoneNumberAndCreatedAtBefore(String phoneNumber, LocalDateTime before);
}
