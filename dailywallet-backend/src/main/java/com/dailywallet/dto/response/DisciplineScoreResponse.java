package com.dailywallet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class DisciplineScoreResponse {
    
    private Integer overallScore;
    
    private Integer disciplineScore;
    
    private Integer emergencyReadiness;
    
    private Integer wealthBuilding;
    
    private Integer spendingControl;
    
    private Integer savingConsistency;
    
    private String grade;
    
    private String recommendation;
    
    private LocalDateTime lastCalculated;
    
    private Map<String, Integer> scoreBreakdown;
    
    private Integer streakDays;
    
    private String achievementLevel;
    
    private BigDecimal financialHealthIndex;
    
    private Boolean onTrack;
    
    private String nextMilestone;
    
    private Integer daysToNextMilestone;
}
