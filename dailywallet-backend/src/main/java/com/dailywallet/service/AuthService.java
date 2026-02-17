package com.dailywallet.service;

import com.dailywallet.dto.request.LoginRequest;
import com.dailywallet.dto.request.RegisterRequest;
import com.dailywallet.dto.request.VerifyOtpRequest;
import com.dailywallet.dto.response.AuthResponse;
import com.dailywallet.dto.response.UserResponse;
import com.dailywallet.exception.BusinessException;
import com.dailywallet.model.entity.OtpVerification;
import com.dailywallet.model.entity.User;
import com.dailywallet.model.entity.Wallet;
import com.dailywallet.model.enums.WalletType;
import com.dailywallet.repository.OtpVerificationRepository;
import com.dailywallet.repository.UserRepository;
import com.dailywallet.security.JwtUtil;
import com.dailywallet.util.ReferenceNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final OtpVerificationRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Value("${app.otp.expiration-minutes}")
    private int otpExpirationMinutes;

    @Value("${app.otp.length}")
    private int otpLength;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BusinessException("Phone number already registered");
        }

        User user = User.builder()
                .phoneNumber(request.getPhoneNumber())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneVerified(false)
                .active(true)
                .onboardingCompleted(false)
                .wallets(new ArrayList<>())
                .build();

        List<Wallet> wallets = createDefaultWallets(user);
        user.setWallets(wallets);

        user = userRepository.save(user);

        String token = jwtUtil.generateToken(user.getPhoneNumber());

        return AuthResponse.builder()
                .token(token)
                .user(mapToUserResponse(user))
                .onboardingCompleted(false)
                .build();
    }

    @Transactional
    public void sendOtp(String phoneNumber) {
        String otpCode = ReferenceNumberGenerator.generateOtp(otpLength);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(otpExpirationMinutes);

        otpRepository.deleteByPhoneNumber(phoneNumber);

        OtpVerification otp = OtpVerification.builder()
                .phoneNumber(phoneNumber)
                .otpCode(otpCode)
                .expiresAt(expiresAt)
                .verified(false)
                .attempts(0)
                .build();

        otpRepository.save(otp);

        log.info("=".repeat(80));
        log.info("📱 OTP CODE FOR PHONE: {}", phoneNumber);
        log.info("🔐 OTP CODE: {}", otpCode);
        log.info("⏰ EXPIRES AT: {}", expiresAt);
        log.info("⏳ VALID FOR: {} minutes", otpExpirationMinutes);
        log.info("=".repeat(80));
    }

    @Transactional
    public void verifyOtp(VerifyOtpRequest request) {
        log.info("🔍 OTP Verification attempt for phone: {}", request.getPhoneNumber());
        
        OtpVerification otp = otpRepository.findByPhoneNumberAndOtpCode(
                        request.getPhoneNumber(),
                        request.getOtpCode())
                .orElseThrow(() -> {
                    log.error("❌ Invalid OTP code provided for phone: {}", request.getPhoneNumber());
                    return new BusinessException("Invalid OTP code");
                });

        if (otp.getVerified()) {
            log.warn("⚠️ OTP already used for phone: {}", request.getPhoneNumber());
            throw new BusinessException("OTP already used");
        }

        if (LocalDateTime.now().isAfter(otp.getExpiresAt())) {
            log.error("⏰ OTP expired for phone: {}. Expired at: {}", request.getPhoneNumber(), otp.getExpiresAt());
            throw new BusinessException("OTP has expired");
        }

        if (otp.getAttempts() >= 3) {
            log.error("🚫 Maximum OTP attempts exceeded for phone: {}", request.getPhoneNumber());
            throw new BusinessException("Maximum OTP attempts exceeded");
        }

        otp.setVerified(true);
        otp.setVerifiedAt(LocalDateTime.now());
        otpRepository.save(otp);

        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new BusinessException("User not found"));
        user.setPhoneVerified(true);
        userRepository.save(user);

        log.info("✅ Phone number verified successfully: {}", request.getPhoneNumber());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getPhoneNumber(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new BusinessException("User not found"));

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getPhoneNumber());

        return AuthResponse.builder()
                .token(token)
                .user(mapToUserResponse(user))
                .onboardingCompleted(user.getOnboardingCompleted())
                .build();
    }

    private List<Wallet> createDefaultWallets(User user) {
        List<Wallet> wallets = new ArrayList<>();
        
        for (WalletType type : WalletType.values()) {
            Wallet wallet = Wallet.builder()
                    .user(user)
                    .walletType(type)
                    .balance(BigDecimal.ZERO)
                    .lockedBalance(BigDecimal.ZERO)
                    .totalDeposited(BigDecimal.ZERO)
                    .totalWithdrawn(BigDecimal.ZERO)
                    .totalInterestEarned(BigDecimal.ZERO)
                    .build();
            wallets.add(wallet);
        }
        
        return wallets;
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneVerified(user.getPhoneVerified())
                .onboardingCompleted(user.getOnboardingCompleted())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
