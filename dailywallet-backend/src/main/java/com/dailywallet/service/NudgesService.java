package com.dailywallet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class NudgesService {

    public List<Object> getPersonalizedNudges(String phoneNumber) {
        List<Object> nudges = new ArrayList<>();
        
        // Mock personalized nudges based on user behavior
        Map<String, Object> nudge1 = new HashMap<>();
        nudge1.put("id", "nudge_001");
        nudge1.put("type", "SPENDING_CONTROL");
        nudge1.put("title", "Great job on daily spending!");
        nudge1.put("message", "You've stayed within your daily allowance for 7 days straight. Keep it up!");
        nudge1.put("priority", "HIGH");
        nudge1.put("actionable", true);
        nudge1.put("actionText", "View Progress");
        nudge1.put("category", "POSITIVE_REINFORCEMENT");
        nudge1.put("timestamp", System.currentTimeMillis());
        
        Map<String, Object> nudge2 = new HashMap<>();
        nudge2.put("id", "nudge_002");
        nudge2.put("type", "EMERGENCY_FUND");
        nudge2.put("title", "Emergency fund progress update");
        nudge2.put("message", "You're 73% towards your KES 3,000 emergency fund goal. Add KES 810 to complete it!");
        nudge2.put("priority", "MEDIUM");
        nudge2.put("actionable", true);
        nudge2.put("actionText", "Add to Emergency Fund");
        nudge2.put("category", "GOAL_TRACKING");
        nudge2.put("timestamp", System.currentTimeMillis());
        
        Map<String, Object> nudge3 = new HashMap<>();
        nudge3.put("id", "nudge_003");
        nudge3.put("type", "MMF_OPPORTUNITY");
        nudge3.put("title", "MMF sweep opportunity");
        nudge3.put("message", "You have KES 1,250 available for MMF sweep. Expected monthly yield: KES 15");
        nudge3.put("priority", "LOW");
        nudge3.put("actionable", true);
        nudge3.put("actionText", "Execute Sweep");
        nudge3.put("category", "WEALTH_BUILDING");
        nudge3.put("timestamp", System.currentTimeMillis());
        
        nudges.add(nudge1);
        nudges.add(nudge2);
        nudges.add(nudge3);
        
        return nudges;
    }

    public Object getSpendingInsights(String phoneNumber) {
        Map<String, Object> insights = new HashMap<>();
        
        insights.put("averageDailySpending", 450);
        insights.put("spendingTrend", "decreasing");
        insights.put("topCategory", "Food & Groceries");
        insights.put("budgetUtilization", 75);
        insights.put("savingsRate", 25);
        insights.put("disciplineScore", 87);
        insights.put("weekendSpending", 180);
        insights.put("weekdaySpending", 270);
        insights.put("recommendations", Arrays.asList(
            "Reduce weekend spending by 20%",
            "Increase emergency fund contributions",
            "Consider MMF sweep for idle balances"
        ));
        
        List<Map<String, Object>> categoryBreakdown = new ArrayList<>();
        Map<String, Object> food = new HashMap<>();
        food.put("category", "Food & Groceries");
        food.put("amount", 180);
        food.put("percentage", 40);
        categoryBreakdown.add(food);
        
        Map<String, Object> transport = new HashMap<>();
        transport.put("category", "Transport");
        transport.put("amount", 135);
        transport.put("percentage", 30);
        categoryBreakdown.add(transport);
        
        Map<String, Object> other = new HashMap<>();
        other.put("category", "Other");
        other.put("amount", 135);
        other.put("percentage", 30);
        categoryBreakdown.add(other);
        
        insights.put("categoryBreakdown", categoryBreakdown);
        
        return insights;
    }

    public void actOnNudge(String phoneNumber, String nudgeId, Object actionRequest) {
        log.info("User {} acted on nudge {}: {}", phoneNumber, nudgeId, actionRequest);
        
        // Process the nudge action
        Map<String, Object> action = (Map<String, Object>) actionRequest;
        String actionType = (String) action.get("action");
        String feedback = (String) action.get("feedback");
        
        // Store the action for analytics and personalization improvement
        // This would typically be saved to a database
    }

    public Object getBehavioralScores(String phoneNumber) {
        Map<String, Object> scores = new HashMap<>();
        
        scores.put("overallScore", 87);
        scores.put("disciplineScore", 92);
        scores.put("consistencyScore", 85);
        scores.put("planningScore", 88);
        scores.put("resilienceScore", 82);
        
        Map<String, Object> scoreBreakdown = new HashMap<>();
        scoreBreakdown.put("spendingControl", 92);
        scoreBreakdown.put("savingConsistency", 90);
        scoreBreakdown.put("emergencyPreparedness", 73);
        scoreBreakdown.put("investmentBehavior", 85);
        scoreBreakdown.put("debtManagement", 95);
        
        scores.put("scoreBreakdown", scoreBreakdown);
        scores.put("grade", "A");
        scores.put("percentile", 85);
        scores.put("improvementAreas", Arrays.asList("Emergency fund", "Investment consistency"));
        scores.put("strengths", Arrays.asList("Spending control", "Debt management"));
        scores.put("lastUpdated", System.currentTimeMillis());
        
        return scores;
    }

    public List<Object> getRecommendations(String phoneNumber) {
        List<Object> recommendations = new ArrayList<>();
        
        Map<String, Object> rec1 = new HashMap<>();
        rec1.put("id", "rec_001");
        rec1.put("type", "SPENDING_OPTIMIZATION");
        rec1.put("title", "Optimize weekend spending");
        rec1.put("description", "Your weekend spending is 33% higher than weekdays. Consider setting a weekend budget.");
        rec1.put("potentialSavings", 60);
        rec1.put("difficulty", "EASY");
        rec1.put("timeframe", "1 week");
        
        Map<String, Object> rec2 = new HashMap<>();
        rec2.put("id", "rec_002");
        rec2.put("type", "EMERGENCY_FUND");
        rec2.put("title", "Complete emergency fund");
        rec2.put("description", "You're close to your KES 3,000 emergency fund goal. Add KES 810 to reach it.");
        rec1.put("potentialSavings", 0);
        rec2.put("difficulty", "EASY");
        rec2.put("timeframe", "2 weeks");
        
        Map<String, Object> rec3 = new HashMap<>();
        rec3.put("id", "rec_003");
        rec3.put("type", "WEALTH_BUILDING");
        rec3.put("title", "Enable MMF auto-sweep");
        rec3.put("description", "Automatically invest idle balances to earn 1.2% monthly returns.");
        rec3.put("potentialSavings", 15);
        rec3.put("difficulty", "EASY");
        rec3.put("timeframe", "Immediate");
        
        recommendations.add(rec1);
        recommendations.add(rec2);
        recommendations.add(rec3);
        
        return recommendations;
    }

    public Object getProgressMilestones(String phoneNumber) {
        Map<String, Object> milestones = new HashMap<>();
        
        List<Map<String, Object>> achievements = new ArrayList<>();
        
        Map<String, Object> achievement1 = new HashMap<>();
        achievement1.put("id", "milestone_001");
        achievement1.put("title", "7-Day Spending Champion");
        achievement1.put("description", "Stayed within daily allowance for 7 consecutive days");
        achievement1.put("achievedAt", System.currentTimeMillis() - 86400000); // 1 day ago
        achievement1.put("category", "DISCIPLINE");
        achievement1.put("points", 50);
        
        Map<String, Object> achievement2 = new HashMap<>();
        achievement2.put("id", "milestone_002");
        achievement2.put("title", "Emergency Fund Builder");
        achievement2.put("description", "Reached 50% of emergency fund goal");
        achievement2.put("achievedAt", System.currentTimeMillis() - 172800000); // 2 days ago
        achievement2.put("category", "SAVINGS");
        achievement2.put("points", 75);
        
        Map<String, Object> achievement3 = new HashMap<>();
        achievement3.put("id", "milestone_003");
        achievement3.put("title", "MMF Investor");
        achievement3.put("description", "First MMF investment completed");
        achievement3.put("achievedAt", System.currentTimeMillis() - 604800000); // 1 week ago
        achievement3.put("category", "WEALTH");
        achievement3.put("points", 100);
        
        achievements.add(achievement1);
        achievements.add(achievement2);
        achievements.add(achievement3);
        
        milestones.put("achievements", achievements);
        milestones.put("totalPoints", 225);
        milestones.put("currentLevel", "Financial Builder");
        milestones.put("nextLevel", "Wealth Creator");
        milestones.put("pointsToNextLevel", 75);
        milestones.put("currentStreak", 14);
        milestones.put("longestStreak", 23);
        
        return milestones;
    }

    public void submitFeedback(String phoneNumber, Object feedbackRequest) {
        Map<String, Object> feedback = (Map<String, Object>) feedbackRequest;
        String nudgeId = (String) feedback.get("nudgeId");
        String rating = (String) feedback.get("rating");
        String comment = (String) feedback.get("comment");
        
        log.info("Feedback received for nudge {}: rating={}, comment={}", nudgeId, rating, comment);
        
        // Store feedback for improving nudge relevance and effectiveness
        // This would typically be saved to a database for analytics
    }
}
