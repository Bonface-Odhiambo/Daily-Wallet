package com.dailywallet.controller;

import com.dailywallet.dto.request.LoginRequest;
import com.dailywallet.dto.request.RegisterRequest;
import com.dailywallet.dto.request.VerifyOtpRequest;
import com.dailywallet.dto.response.ApiResponse;
import com.dailywallet.dto.response.AuthResponse;
import com.dailywallet.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Create a new user account with phone number and password")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        authService.sendOtp(request.getPhoneNumber());
        return ResponseEntity.ok(ApiResponse.success("Registration successful. OTP sent to your phone.", response));
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with phone number and password")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<Void>> sendOtp(@RequestParam String phoneNumber) {
        authService.sendOtp(phoneNumber);
        return ResponseEntity.ok(ApiResponse.success("OTP sent successfully", null));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        authService.verifyOtp(request);
        return ResponseEntity.ok(ApiResponse.success("Phone number verified successfully", null));
    }
}
