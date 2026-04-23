package com.dailywallet.controller;

import com.dailywallet.dto.response.ApiResponse;
import com.dailywallet.service.NudgesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/nudges")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Financial Nudges", description = "AI-powered financial recommendations and behavioral insights")
@SecurityRequirement(name = "Bearer Authentication")
public class NudgesController {

    private final NudgesService nudgesService;

    @GetMapping("/personalized")
    @Operation(summary = "Get personalized nudges", description = "Retrieve AI-powered financial recommendations")
    public ResponseEntity<ApiResponse<List<Object>>> getPersonalizedNudges(Authentication authentication) {
        List<Object> nudges = nudgesService.getPersonalizedNudges(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Personalized nudges retrieved", nudges));
    }

    @GetMapping("/spending-insights")
    @Operation(summary = "Get spending insights", description = "Analyze spending patterns and provide recommendations")
    public ResponseEntity<ApiResponse<Object>> getSpendingInsights(Authentication authentication) {
        Object insights = nudgesService.getSpendingInsights(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Spending insights generated", insights));
    }

    @PostMapping("/{nudgeId}/action")
    @Operation(summary = "Act on nudge", description = "Record user action on financial recommendation")
    public ResponseEntity<ApiResponse<Void>> actOnNudge(
            @PathVariable String nudgeId,
            @RequestBody Map<String, Object> actionRequest,
            Authentication authentication) {
        nudgesService.actOnNudge(authentication.getName(), nudgeId, actionRequest);
        return ResponseEntity.ok(ApiResponse.success("Nudge action recorded", null));
    }

    @GetMapping("/behavioral-scores")
    @Operation(summary = "Get behavioral scores", description = "Retrieve detailed behavioral scoring metrics")
    public ResponseEntity<ApiResponse<Object>> getBehavioralScores(Authentication authentication) {
        Object scores = nudgesService.getBehavioralScores(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Behavioral scores retrieved", scores));
    }

    @GetMapping("/recommendations")
    @Operation(summary = "Get recommendations", description = "Get actionable financial recommendations")
    public ResponseEntity<ApiResponse<List<Object>>> getRecommendations(Authentication authentication) {
        List<Object> recommendations = nudgesService.getRecommendations(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Recommendations retrieved", recommendations));
    }

    @GetMapping("/progress-milestones")
    @Operation(summary = "Get progress milestones", description = "Track financial goal achievements")
    public ResponseEntity<ApiResponse<Object>> getProgressMilestones(Authentication authentication) {
        Object milestones = nudgesService.getProgressMilestones(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Progress milestones retrieved", milestones));
    }

    @PostMapping("/feedback")
    @Operation(summary = "Submit feedback", description="Provide feedback on nudge usefulness")
    public ResponseEntity<ApiResponse<Void>> submitFeedback(
            @RequestBody Map<String, Object> feedbackRequest,
            Authentication authentication) {
        nudgesService.submitFeedback(authentication.getName(), feedbackRequest);
        return ResponseEntity.ok(ApiResponse.success("Feedback submitted", null));
    }
}
