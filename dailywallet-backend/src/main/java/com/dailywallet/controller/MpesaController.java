package com.dailywallet.controller;

import com.dailywallet.service.MpesaService;
import com.dailywallet.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/mpesa")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "M-Pesa API", description = "M-Pesa payment integration endpoints")
public class MpesaController {

    private final MpesaService mpesaService;

    @PostMapping("/stk-push")
    @Operation(summary = "Initiate STK Push", description = "Initiate M-Pesa STK Push for payment")
    public ResponseEntity<ApiResponse<Map<String, Object>>> initiateStkPush(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        
        String phoneNumber = (String) request.get("phoneNumber");
        Integer amount = (Integer) request.get("amount");
        String accountReference = (String) request.get("accountReference");
        String transactionDesc = (String) request.get("transactionDesc");
        
        log.info("Initiating STK Push for user {}: amount {}", authentication.getName(), amount);
        
        Map<String, Object> result = mpesaService.initiateStkPush(
                authentication.getName(), 
                phoneNumber, 
                amount, 
                accountReference, 
                transactionDesc
        );
        
        return ResponseEntity.ok(ApiResponse.success("STK Push initiated successfully", result));
    }

    @PostMapping("/deposit")
    @Operation(summary = "Initiate Deposit", description = "Initiate M-Pesa deposit to wallet")
    public ResponseEntity<ApiResponse<Map<String, Object>>> initiateDeposit(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        
        Integer amount = (Integer) request.get("amount");
        String phoneNumber = (String) request.get("phoneNumber");
        
        log.info("Initiating M-Pesa deposit for user {}: amount {}", authentication.getName(), amount);
        
        Map<String, Object> result = mpesaService.initiateDeposit(
                authentication.getName(), 
                phoneNumber, 
                amount
        );
        
        return ResponseEntity.ok(ApiResponse.success("M-Pesa deposit initiated", result));
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Initiate Withdrawal", description = "Initiate M-Pesa withdrawal from wallet")
    public ResponseEntity<ApiResponse<Map<String, Object>>> initiateWithdrawal(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        
        Integer amount = (Integer) request.get("amount");
        String phoneNumber = (String) request.get("phoneNumber");
        
        log.info("Initiating M-Pesa withdrawal for user {}: amount {}", authentication.getName(), amount);
        
        Map<String, Object> result = mpesaService.initiateWithdrawal(
                authentication.getName(), 
                phoneNumber, 
                amount
        );
        
        return ResponseEntity.ok(ApiResponse.success("M-Pesa withdrawal initiated", result));
    }

    @GetMapping("/status/{checkoutRequestId}")
    @Operation(summary = "Check Transaction Status", description = "Check the status of an M-Pesa transaction")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkTransactionStatus(
            @PathVariable String checkoutRequestId,
            Authentication authentication) {
        
        log.info("Checking M-Pesa transaction status for user {}: {}", authentication.getName(), checkoutRequestId);
        
        Map<String, Object> result = mpesaService.checkTransactionStatus(checkoutRequestId);
        
        return ResponseEntity.ok(ApiResponse.success("Transaction status retrieved", result));
    }
}
