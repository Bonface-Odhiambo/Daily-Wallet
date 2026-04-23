package com.dailywallet.controller;

import com.dailywallet.dto.request.MMFSweepRequest;
import com.dailywallet.dto.response.ApiResponse;
import com.dailywallet.dto.response.MMFInvestmentResponse;
import com.dailywallet.service.MMFService;
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
@RequestMapping("/api/mmf")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "MMF Growth Bucket", description = "Money market fund investment and sweep endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class MMFController {

    private final MMFService mmfService;

    @GetMapping("/investments")
    @Operation(summary = "Get MMF investments", description = "Retrieve current MMF investment details and performance")
    public ResponseEntity<ApiResponse<List<MMFInvestmentResponse>>> getMMFInvestments(Authentication authentication) {
        List<MMFInvestmentResponse> investments = mmfService.getMMFInvestments(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("MMF investments retrieved", investments));
    }

    @PostMapping("/sweep")
    @Operation(summary = "Trigger MMF sweep", description = "Execute automatic sweep of surplus funds to MMF")
    public ResponseEntity<ApiResponse<Object>> triggerMMFSweep(
            @Valid @RequestBody MMFSweepRequest request,
            Authentication authentication) {
        Object result = mmfService.executeSweep(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("MMF sweep executed successfully", result));
    }

    @GetMapping("/yield-history")
    @Operation(summary = "Get MMF yield history", description = "Retrieve historical yield performance data")
    public ResponseEntity<ApiResponse<Object>> getYieldHistory(
            @RequestParam(defaultValue = "monthly") String period,
            Authentication authentication) {
        Object history = mmfService.getYieldHistory(authentication.getName(), period);
        return ResponseEntity.ok(ApiResponse.success("Yield history retrieved", history));
    }

    @GetMapping("/performance")
    @Operation(summary = "Get MMF performance metrics", description = "Calculate investment performance and returns")
    public ResponseEntity<ApiResponse<Object>> getPerformanceMetrics(Authentication authentication) {
        Object metrics = mmfService.getPerformanceMetrics(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Performance metrics retrieved", metrics));
    }

    @PostMapping("/auto-sweep-toggle")
    @Operation(summary = "Toggle auto-sweep", description = "Enable or disable automatic MMF sweeping")
    public ResponseEntity<ApiResponse<Void>> toggleAutoSweep(
            @RequestParam boolean enabled,
            Authentication authentication) {
        mmfService.toggleAutoSweep(authentication.getName(), enabled);
        return ResponseEntity.ok(ApiResponse.success("Auto-sweep setting updated", null));
    }

    @GetMapping("/sweep-preview")
    @Operation(summary = "Get sweep preview", description = "Preview upcoming MMF sweep amount and timing")
    public ResponseEntity<ApiResponse<Object>> getSweepPreview(Authentication authentication) {
        Object preview = mmfService.getSweepPreview(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Sweep preview generated", preview));
    }
}
