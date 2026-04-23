package com.dailywallet.controller;

import com.dailywallet.dto.request.FeeBreakdownRequest;
import com.dailywallet.dto.response.ApiResponse;
import com.dailywallet.dto.response.FeeBreakdownResponse;
import com.dailywallet.service.TransparencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transparency")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Transparency Engine", description = "Fee breakdown and cost transparency endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class TransparencyController {

    private final TransparencyService transparencyService;

    @PostMapping("/fee-breakdown")
    @Operation(summary = "Get fee breakdown", description = "Calculate complete fee breakdown for any transaction")
    public ResponseEntity<ApiResponse<FeeBreakdownResponse>> getFeeBreakdown(
            @Valid @RequestBody FeeBreakdownRequest request,
            Authentication authentication) {
        FeeBreakdownResponse breakdown = transparencyService.calculateFeeBreakdown(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Fee breakdown calculated", breakdown));
    }

    @GetMapping("/cost-breakdown")
    @Operation(summary = "Get transaction cost breakdown", description = "Retrieve detailed cost breakdown for a specific transaction")
    public ResponseEntity<ApiResponse<Object>> getCostBreakdown(
            @RequestParam String transactionId,
            Authentication authentication) {
        Object breakdown = transparencyService.getTransactionCostBreakdown(authentication.getName(), transactionId);
        return ResponseEntity.ok(ApiResponse.success("Cost breakdown retrieved", breakdown));
    }

    @GetMapping("/pricing-model")
    @Operation(summary = "Get pricing model", description = "Display current pricing model and fee structure")
    public ResponseEntity<ApiResponse<Object>> getPricingModel(Authentication authentication) {
        Object pricingModel = transparencyService.getPricingModel(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Pricing model retrieved", pricingModel));
    }

    @GetMapping("/fee-history")
    @Operation(summary = "Get fee history", description = "Retrieve historical fee charges for transparency")
    public ResponseEntity<ApiResponse<Object>> getFeeHistory(
            @RequestParam(defaultValue = "30") int days,
            Authentication authentication) {
        Object history = transparencyService.getFeeHistory(authentication.getName(), days);
        return ResponseEntity.ok(ApiResponse.success("Fee history retrieved", history));
    }
}
