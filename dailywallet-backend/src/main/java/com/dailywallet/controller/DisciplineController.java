package com.dailywallet.controller;

import com.dailywallet.dto.request.DailyAllowanceRequest;
import com.dailywallet.dto.request.ReallocateRequest;
import com.dailywallet.dto.response.ApiResponse;
import com.dailywallet.dto.response.DisciplineScoreResponse;
import com.dailywallet.dto.response.DisciplineBucketResponse;
import com.dailywallet.service.DisciplineService;
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
@RequestMapping("/api/discipline")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Jimudu Wallet Discipline Engine", description = "Core discipline enforcement and financial health endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class DisciplineController {

    private final DisciplineService disciplineService;

    @GetMapping("/buckets")
    @Operation(summary = "Get discipline buckets", description = "Retrieve all Jimudu Wallet discipline buckets with balances and rules")
    public ResponseEntity<ApiResponse<List<DisciplineBucketResponse>>> getDisciplineBuckets(Authentication authentication) {
        List<DisciplineBucketResponse> buckets = disciplineService.getDisciplineBuckets(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Discipline buckets retrieved successfully", buckets));
    }

    @PostMapping("/daily-allowance")
    @Operation(summary = "Set daily allowance", description = "Configure daily spending allowance and emergency savings target")
    public ResponseEntity<ApiResponse<Void>> setDailyAllowance(
            @Valid @RequestBody DailyAllowanceRequest request,
            Authentication authentication) {
        disciplineService.setDailyAllowance(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Daily allowance configured successfully", null));
    }

    @GetMapping("/health-score")
    @Operation(summary = "Get financial health score", description = "Calculate comprehensive financial health metrics")
    public ResponseEntity<ApiResponse<DisciplineScoreResponse>> getFinancialHealthScore(Authentication authentication) {
        DisciplineScoreResponse score = disciplineService.calculateFinancialHealthScore(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Financial health score calculated", score));
    }

    @GetMapping("/streaks")
    @Operation(summary = "Get discipline streaks", description = "Retrieve current discipline streaks and achievements")
    public ResponseEntity<ApiResponse<Object>> getDisciplineStreaks(Authentication authentication) {
        Object streaks = disciplineService.getDisciplineStreaks(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Discipline streaks retrieved", streaks));
    }

    @PostMapping("/reallocate")
    @Operation(summary = "Reallocate funds", description = "Move funds between discipline buckets with governance rules")
    public ResponseEntity<ApiResponse<Void>> reallocateFunds(
            @Valid @RequestBody ReallocateRequest request,
            Authentication authentication) {
        disciplineService.reallocateFunds(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Funds reallocated successfully", null));
    }

    @GetMapping("/spending-insights")
    @Operation(summary = "Get spending insights", description = "Analyze spending patterns and provide recommendations")
    public ResponseEntity<ApiResponse<Object>> getSpendingInsights(Authentication authentication) {
        Object insights = disciplineService.getSpendingInsights(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Spending insights generated", insights));
    }

    @GetMapping("/emergency-progress")
    @Operation(summary = "Get emergency savings progress", description = "Track emergency fund goal progress")
    public ResponseEntity<ApiResponse<Object>> getEmergencyProgress(Authentication authentication) {
        Object progress = disciplineService.getEmergencyProgress(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Emergency fund progress retrieved", progress));
    }

    @PostMapping("/nudge/{nudgeId}/action")
    @Operation(summary = "Act on financial nudge", description = "Record user action on financial recommendation")
    public ResponseEntity<ApiResponse<Void>> actOnNudge(
            @PathVariable String nudgeId,
            @RequestBody Object actionRequest,
            Authentication authentication) {
        disciplineService.actOnNudge(authentication.getName(), nudgeId, actionRequest);
        return ResponseEntity.ok(ApiResponse.success("Nudge action recorded", null));
    }
}
