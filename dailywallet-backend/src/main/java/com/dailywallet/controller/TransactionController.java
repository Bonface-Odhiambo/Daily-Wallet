package com.dailywallet.controller;

import com.dailywallet.dto.request.DepositRequest;
import com.dailywallet.dto.request.WithdrawRequest;
import com.dailywallet.dto.response.ApiResponse;
import com.dailywallet.dto.response.TransactionResponse;
import com.dailywallet.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Transactions", description = "Transaction management including M-Pesa deposits and withdrawals")
@SecurityRequirement(name = "Bearer Authentication")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    @Operation(summary = "Initiate M-Pesa deposit", description = "Send STK push to user's phone for deposit. Funds are automatically allocated based on user's allocation rules.")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(
            @Valid @RequestBody DepositRequest request,
            Authentication authentication) {
        TransactionResponse transaction = transactionService.initiateDeposit(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Deposit initiated. Please complete payment on your phone.", transaction));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdraw(
            @Valid @RequestBody WithdrawRequest request,
            Authentication authentication) {
        TransactionResponse transaction = transactionService.initiateWithdrawal(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Withdrawal processed successfully", transaction));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getTransactions(
            Authentication authentication,
            Pageable pageable) {
        Page<TransactionResponse> transactions = transactionService.getUserTransactions(authentication.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getRecentTransactions(
            Authentication authentication,
            @RequestParam(defaultValue = "10") int limit) {
        List<TransactionResponse> transactions = transactionService.getRecentTransactions(authentication.getName(), limit);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }
}
