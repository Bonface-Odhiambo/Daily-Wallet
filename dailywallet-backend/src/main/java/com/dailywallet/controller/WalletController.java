package com.dailywallet.controller;

import com.dailywallet.dto.request.ReallocateRequest;
import com.dailywallet.dto.request.SetAllocationRequest;
import com.dailywallet.dto.response.ApiResponse;
import com.dailywallet.dto.response.WalletResponse;
import com.dailywallet.model.enums.WalletType;
import com.dailywallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Wallets", description = "Wallet management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    @Operation(summary = "Get all wallets", description = "Retrieve all wallets (DAILY, WEEKLY, MONTHLY, SAVINGS) for the authenticated user with balances and release times")
    public ResponseEntity<ApiResponse<List<WalletResponse>>> getUserWallets(Authentication authentication) {
        List<WalletResponse> wallets = walletService.getUserWallets(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(wallets));
    }

    @GetMapping("/{walletType}")
    public ResponseEntity<ApiResponse<WalletResponse>> getWalletByType(
            @PathVariable WalletType walletType,
            Authentication authentication) {
        WalletResponse wallet = walletService.getWalletByType(authentication.getName(), walletType);
        return ResponseEntity.ok(ApiResponse.success(wallet));
    }

    @PostMapping("/allocation")
    public ResponseEntity<ApiResponse<Void>> setAllocation(
            @Valid @RequestBody SetAllocationRequest request,
            Authentication authentication) {
        walletService.setAllocationRule(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Allocation rule set successfully", null));
    }

    @PostMapping("/reallocate")
    public ResponseEntity<ApiResponse<Void>> reallocateFunds(
            @Valid @RequestBody ReallocateRequest request,
            Authentication authentication) {
        walletService.reallocateFunds(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Funds reallocated successfully", null));
    }
}
