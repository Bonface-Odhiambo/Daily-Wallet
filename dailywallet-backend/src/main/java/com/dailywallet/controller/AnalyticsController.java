package com.dailywallet.controller;

import com.dailywallet.dto.response.ApiResponse;
import com.dailywallet.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Financial Analytics", description = "Financial wellness metrics and behavioral analytics")
@SecurityRequirement(name = "Bearer Authentication")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/wellness-metrics")
    @Operation(summary = "Get wellness metrics", description = "Calculate comprehensive financial wellness indicators")
    public ResponseEntity<ApiResponse<Object>> getWellnessMetrics(Authentication authentication) {
        Object metrics = analyticsService.getWellnessMetrics(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Wellness metrics retrieved", metrics));
    }

    @GetMapping("/spending-patterns")
    @Operation(summary = "Get spending patterns", description = "Analyze spending behavior and categorization")
    public ResponseEntity<ApiResponse<Object>> getSpendingPatterns(
            @RequestParam(defaultValue = "30") int period,
            Authentication authentication) {
        Object patterns = analyticsService.getSpendingPatterns(authentication.getName(), period);
        return ResponseEntity.ok(ApiResponse.success("Spending patterns analyzed", patterns));
    }

    @GetMapping("/emergency-progress")
    @Operation(summary = "Get emergency progress", description = "Track emergency savings goal progress")
    public ResponseEntity<ApiResponse<Object>> getEmergencyProgress(Authentication authentication) {
        Object progress = analyticsService.getEmergencyProgress(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Emergency progress retrieved", progress));
    }

    @GetMapping("/financial-health-trends")
    @Operation(summary = "Get financial health trends", description = "Track financial health score over time")
    public ResponseEntity<ApiResponse<Object>> getFinancialHealthTrends(
            @RequestParam(defaultValue = "90") int days,
            Authentication authentication) {
        Object trends = analyticsService.getFinancialHealthTrends(authentication.getName(), days);
        return ResponseEntity.ok(ApiResponse.success("Financial health trends retrieved", trends));
    }

    @GetMapping("/discipline-metrics")
    @Operation(summary = "Get discipline metrics", description = "Detailed discipline scoring and streak analysis")
    public ResponseEntity<ApiResponse<Object>> getDisciplineMetrics(Authentication authentication) {
        Object metrics = analyticsService.getDisciplineMetrics(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Discipline metrics retrieved", metrics));
    }

    @GetMapping("/wealth-building")
    @Operation(summary = "Get wealth building progress", description = "Track MMF growth and wealth accumulation")
    public ResponseEntity<ApiResponse<Object>> getWealthBuildingProgress(Authentication authentication) {
        Object progress = analyticsService.getWealthBuildingProgress(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Wealth building progress retrieved", progress));
    }

    @GetMapping("/monthly-summary")
    @Operation(summary = "Get monthly summary", description = "Comprehensive monthly financial summary")
    public ResponseEntity<ApiResponse<Object>> getMonthlySummary(
            @RequestParam int year,
            @RequestParam int month,
            Authentication authentication) {
        Object summary = analyticsService.getMonthlySummary(authentication.getName(), year, month);
        return ResponseEntity.ok(ApiResponse.success("Monthly summary generated", summary));
    }

    @GetMapping("/comparative-analysis")
    @Operation(summary = "Get comparative analysis", description = "Compare user metrics with peer groups")
    public ResponseEntity<ApiResponse<Object>> getComparativeAnalysis(Authentication authentication) {
        Object analysis = analyticsService.getComparativeAnalysis(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Comparative analysis completed", analysis));
    }
}
