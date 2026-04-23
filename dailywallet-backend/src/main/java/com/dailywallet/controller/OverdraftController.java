package com.dailywallet.controller;

import com.dailywallet.dto.request.OverdraftApplicationRequest;
import com.dailywallet.dto.response.ApiResponse;
import com.dailywallet.dto.response.OverdraftFacilityResponse;
import com.dailywallet.dto.response.OverdraftPricingResponse;
import com.dailywallet.service.OverdraftService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/overdraft")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Overdraft Facility", description = "Behavior-linked responsible overdraft endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class OverdraftController {

    private final OverdraftService overdraftService;

    @GetMapping("/facility")
    @Operation(summary = "Get overdraft facility", description = "Retrieve current overdraft facility details and limits")
    public ResponseEntity<ApiResponse<OverdraftFacilityResponse>> getOverdraftFacility(Authentication authentication) {
        OverdraftFacilityResponse facility = overdraftService.getOverdraftFacility(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Overdraft facility retrieved", facility));
    }

    @PostMapping("/apply")
    @Operation(summary = "Apply for overdraft", description = "Apply for behavior-linked overdraft with visible pricing")
    public ResponseEntity<ApiResponse<Object>> applyForOverdraft(
            @Valid @RequestBody OverdraftApplicationRequest request,
            Authentication authentication) {
        Object result = overdraftService.applyForOverdraft(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Overdraft application submitted", result));
    }

    @GetMapping("/pricing")
    @Operation(summary = "Get overdraft pricing", description = "Calculate behavior-linked overdraft pricing")
    public ResponseEntity<ApiResponse<OverdraftPricingResponse>> getOverdraftPricing(
            @RequestParam Double amount,
            @RequestParam(required = false) Integer disciplineScore,
            Authentication authentication) {
        OverdraftPricingResponse pricing = overdraftService.calculateOverdraftPricing(
            authentication.getName(), amount, disciplineScore);
        return ResponseEntity.ok(ApiResponse.success("Overdraft pricing calculated", pricing));
    }

    @PostMapping("/draw")
    @Operation(summary = "Draw overdraft", description = "Draw funds from approved overdraft facility")
    public ResponseEntity<ApiResponse<Object>> drawOverdraft(
            @RequestParam Double amount,
            @RequestParam String purpose,
            Authentication authentication) {
        Object result = overdraftService.drawOverdraft(authentication.getName(), amount, purpose);
        return ResponseEntity.ok(ApiResponse.success("Overdraft drawn successfully", result));
    }

    @GetMapping("/repayment-schedule")
    @Operation(summary = "Get repayment schedule", description = "Retrieve overdraft repayment schedule and options")
    public ResponseEntity<ApiResponse<Object>> getRepaymentSchedule(Authentication authentication) {
        Object schedule = overdraftService.getRepaymentSchedule(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Repayment schedule retrieved", schedule));
    }

    @GetMapping("/usage-history")
    @Operation(summary = "Get overdraft usage history", description = "Retrieve historical overdraft usage and repayment patterns")
    public ResponseEntity<ApiResponse<Object>> getUsageHistory(
            @RequestParam(defaultValue = "90") int days,
            Authentication authentication) {
        Object history = overdraftService.getUsageHistory(authentication.getName(), days);
        return ResponseEntity.ok(ApiResponse.success("Overdraft usage history retrieved", history));
    }

    @PostMapping("/pre-approval-check")
    @Operation(summary = "Pre-approval check", description = "Check overdraft eligibility and estimated limit")
    public ResponseEntity<ApiResponse<Object>> preApprovalCheck(Authentication authentication) {
        Object checkResult = overdraftService.preApprovalCheck(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Pre-approval check completed", checkResult));
    }
}
