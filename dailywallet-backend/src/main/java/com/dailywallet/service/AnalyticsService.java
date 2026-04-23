package com.dailywallet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    public Object getWellnessMetrics(String phoneNumber) {
        Map<String, Object> metrics = new HashMap<>();
        
        metrics.put("overallScore", 87);
        metrics.put("grade", "A");
        metrics.put("financialHealthIndex", BigDecimal.valueOf(0.87));
        metrics.put("disciplineScore", 92);
        metrics.put("emergencyReadiness", 73);
        metrics.put("wealthBuilding", 85);
        metrics.put("spendingControl", 88);
        metrics.put("savingConsistency", 90);
        
        Map<String, Object> keyMetrics = new HashMap<>();
        keyMetrics.put("totalBalance", 18750);
        keyMetrics.put("monthlyIncome", 18000);
        keyMetrics.put("monthlyExpenses", 13500);
        keyMetrics.put("monthlySavings", 4500);
        keyMetrics.put("savingsRate", 25);
        keyMetrics.put("emergencyFundRatio", 16.7);
        
        metrics.put("keyMetrics", keyMetrics);
        
        List<Map<String, Object>> trends = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Map<String, Object> trend = new HashMap<>();
            trend.put("month", LocalDateTime.now().minusMonths(5 - i).getMonth().toString());
            trend.put("score", 80 + (i * 2));
            trend.put("savings", 3000 + (i * 250));
            trend.put("expenses", 12000 + (i * 300));
            trends.add(trend);
        }
        metrics.put("trends", trends);
        
        metrics.put("recommendationCount", 3);
        metrics.put("achievementCount", 5);
        metrics.put("streakDays", 14);
        
        return metrics;
    }

    public Object getSpendingPatterns(String phoneNumber, int period) {
        Map<String, Object> patterns = new HashMap<>();
        
        patterns.put("period", period);
        patterns.put("totalTransactions", 45);
        patterns.put("totalSpent", 13500);
        patterns.put("averageTransaction", 300);
        patterns.put("averageDailySpending", 450);
        
        List<Map<String, Object>> categoryBreakdown = new ArrayList<>();
        Map<String, Object> food = new HashMap<>();
        food.put("category", "Food & Groceries");
        food.put("amount", 5400);
        food.put("percentage", 40);
        food.put("transactionCount", 18);
        food.put("trend", "stable");
        categoryBreakdown.add(food);
        
        Map<String, Object> transport = new HashMap<>();
        transport.put("category", "Transport");
        transport.put("amount", 4050);
        transport.put("percentage", 30);
        transport.put("transactionCount", 15);
        transport.put("trend", "increasing");
        categoryBreakdown.add(transport);
        
        Map<String, Object> utilities = new HashMap<>();
        utilities.put("category", "Utilities");
        utilities.put("amount", 2700);
        utilities.put("percentage", 20);
        utilities.put("transactionCount", 8);
        utilities.put("trend", "decreasing");
        categoryBreakdown.add(utilities);
        
        Map<String, Object> other = new HashMap<>();
        other.put("category", "Other");
        other.put("amount", 1350);
        other.put("percentage", 10);
        other.put("transactionCount", 4);
        other.put("trend", "stable");
        categoryBreakdown.add(other);
        
        patterns.put("categoryBreakdown", categoryBreakdown);
        
        Map<String, Object> temporalPatterns = new HashMap<>();
        temporalPatterns.put("weekdaySpending", 270);
        temporalPatterns.put("weekendSpending", 180);
        temporalPatterns.put("morningSpending", 150);
        temporalPatterns.put("afternoonSpending", 200);
        temporalPatterns.put("eveningSpending", 100);
        patterns.put("temporalPatterns", temporalPatterns);
        
        patterns.put("budgetUtilization", 75);
        patterns.put("overspendDays", 3);
        patterns.put("underspendDays", 12);
        
        return patterns;
    }

    public Object getEmergencyProgress(String phoneNumber) {
        Map<String, Object> progress = new HashMap<>();
        
        progress.put("targetAmount", 3000);
        progress.put("currentAmount", 2190);
        progress.put("progressPercentage", 73);
        progress.put("remainingAmount", 810);
        progress.put("monthlyContribution", 600);
        progress.put("monthsToComplete", 2);
        progress.put("onTrack", true);
        
        Map<String, Object> milestones = new HashMap<>();
        List<Map<String, Object>> milestoneList = new ArrayList<>();
        
        Map<String, Object> milestone1 = new HashMap<>();
        milestone1.put("percentage", 25);
        milestone1.put("amount", 750);
        milestone1.put("achieved", true);
        milestone1.put("achievedAt", LocalDateTime.now().minusMonths(3));
        milestoneList.add(milestone1);
        
        Map<String, Object> milestone2 = new HashMap<>();
        milestone2.put("percentage", 50);
        milestone2.put("amount", 1500);
        milestone2.put("achieved", true);
        milestone2.put("achievedAt", LocalDateTime.now().minusMonths(1));
        milestoneList.add(milestone2);
        
        Map<String, Object> milestone3 = new HashMap<>();
        milestone3.put("percentage", 75);
        milestone3.put("amount", 2250);
        milestone3.put("achieved", false);
        milestone3.put("estimatedAt", LocalDateTime.now().plusMonths(1));
        milestoneList.add(milestone3);
        
        Map<String, Object> milestone4 = new HashMap<>();
        milestone4.put("percentage", 100);
        milestone4.put("amount", 3000);
        milestone4.put("achieved", false);
        milestone4.put("estimatedAt", LocalDateTime.now().plusMonths(2));
        milestoneList.add(milestone4);
        
        progress.put("milestones", milestoneList);
        
        progress.put("lastContribution", LocalDateTime.now().minusDays(7));
        progress.put("contributionStreak", 8);
        progress.put("averageContribution", 375);
        
        return progress;
    }

    public Object getFinancialHealthTrends(String phoneNumber, int days) {
        List<Map<String, Object>> trends = new ArrayList<>();
        
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        for (int i = 0; i < days; i++) {
            Map<String, Object> dayData = new HashMap<>();
            LocalDateTime date = startDate.plusDays(i);
            
            dayData.put("date", date.toLocalDate().toString());
            dayData.put("overallScore", 80 + (i % 15));
            dayData.put("disciplineScore", 85 + (i % 10));
            dayData.put("emergencyReadiness", 70 + (i % 20));
            dayData.put("wealthBuilding", 82 + (i % 12));
            dayData.put("spendingControl", 88 + (i % 8));
            dayData.put("savingConsistency", 90 + (i % 5));
            
            trends.add(dayData);
        }
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("trends", trends);
        summary.put("averageScore", 87);
        summary.put("highestScore", 95);
        summary.put("lowestScore", 80);
        summary.put("improvementRate", 5.2);
        summary.put("consistencyScore", 92);
        
        return summary;
    }

    public Object getDisciplineMetrics(String phoneNumber) {
        Map<String, Object> metrics = new HashMap<>();
        
        metrics.put("overallDisciplineScore", 92);
        metrics.put("currentStreak", 14);
        metrics.put("longestStreak", 23);
        metrics.put("totalDisciplinedDays", 156);
        
        Map<String, Object> breakdown = new HashMap<>();
        breakdown.put("dailyAllowanceCompliance", 95);
        breakdown.put("spendingControl", 92);
        breakdown.put("savingConsistency", 90);
        breakdown.put("emergencyFundProtection", 88);
        breakdown.put("investmentDiscipline", 85);
        metrics.put("breakdown", breakdown);
        
        List<Map<String, Object>> achievements = new ArrayList<>();
        Map<String, Object> achievement1 = new HashMap<>();
        achievement1.put("title", "7-Day Warrior");
        achievement1.put("description", "7 consecutive days of disciplined spending");
        achievement1.put("achievedAt", LocalDateTime.now().minusDays(7));
        achievement1.put("points", 50);
        achievements.add(achievement1);
        
        Map<String, Object> achievement2 = new HashMap<>();
        achievement2.put("title", "14-Day Champion");
        achievement2.put("description", "14 consecutive days of disciplined spending");
        achievement2.put("achievedAt", LocalDateTime.now().minusDays(1));
        achievement2.put("points", 100);
        achievements.add(achievement2);
        
        metrics.put("achievements", achievements);
        metrics.put("totalPoints", 150);
        metrics.put("nextMilestone", "21-Day Master");
        metrics.put("daysToNextMilestone", 7);
        
        return metrics;
    }

    public Object getWealthBuildingProgress(String phoneNumber) {
        Map<String, Object> progress = new HashMap<>();
        
        progress.put("totalInvested", 15000);
        progress.put("currentValue", 15675);
        progress.put("totalReturns", 675);
        progress.put("returnPercentage", 4.5);
        progress.put("annualizedReturn", 15.6);
        
        Map<String, Object> mmfDetails = new HashMap<>();
        mmfDetails.put("investedAmount", 15000);
        mmfDetails.put("currentValue", 15675);
        mmfDetails.put("monthlyYield", 225);
        mmfDetails.put("yieldRate", 1.2);
        mmfDetails.put("autoSweepEnabled", true);
        mmfDetails.put("sweepAmount", 500);
        mmfDetails.put("sweepFrequency", "daily");
        progress.put("mmfDetails", mmfDetails);
        
        Map<String, Object> goals = new HashMap<>();
        goals.put("shortTermGoal", 20000);
        goals.put("shortTermProgress", 78.38);
        goals.put("longTermGoal", 100000);
        goals.put("longTermProgress", 15.68);
        progress.put("goals", goals);
        
        List<Map<String, Object>> projections = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            Map<String, Object> projection = new HashMap<>();
            projection.put("month", i);
            projection.put("projectedValue", 15675 + (i * 225));
            projection.put("projectedReturns", 675 + (i * 225));
            projections.add(projection);
        }
        progress.put("projections", projections);
        
        return progress;
    }

    public Object getMonthlySummary(String phoneNumber, int year, int month) {
        Map<String, Object> summary = new HashMap<>();
        
        summary.put("year", year);
        summary.put("month", month);
        summary.put("totalIncome", 18000);
        summary.put("totalExpenses", 13500);
        summary.put("totalSavings", 4500);
        summary.put("savingsRate", 25);
        summary.put("netWorthChange", 525);
        
        Map<String, Object> spendingBreakdown = new HashMap<>();
        spendingBreakdown.put("food", 5400);
        spendingBreakdown.put("transport", 4050);
        spendingBreakdown.put("utilities", 2700);
        spendingBreakdown.put("other", 1350);
        summary.put("spendingBreakdown", spendingBreakdown);
        
        summary.put("disciplineScore", 92);
        summary.put("daysWithinBudget", 27);
        summary.put("daysOverBudget", 3);
        summary.put("emergencyFundProgress", 73);
        summary.put("mmfReturns", 225);
        
        List<Map<String, Object>> keyTransactions = new ArrayList<>();
        Map<String, Object> transaction1 = new HashMap<>();
        transaction1.put("date", LocalDateTime.now().minusDays(15));
        transaction1.put("type", "DEPOSIT");
        transaction1.put("amount", 18000);
        transaction1.put("description", "Monthly salary");
        keyTransactions.add(transaction1);
        
        Map<String, Object> transaction2 = new HashMap<>();
        transaction2.put("date", LocalDateTime.now().minusDays(10));
        transaction2.put("type", "MMF_SWEEP");
        transaction2.put("amount", 15000);
        transaction2.put("description", "MMF investment");
        keyTransactions.add(transaction2);
        
        summary.put("keyTransactions", keyTransactions);
        
        return summary;
    }

    public Object getComparativeAnalysis(String phoneNumber) {
        Map<String, Object> analysis = new HashMap<>();
        
        analysis.put("userScore", 87);
        analysis.put("peerAverage", 72);
        analysis.put("peerPercentile", 85);
        analysis.put("topQuartile", 90);
        
        Map<String, Object> comparison = new HashMap<>();
        comparison.put("disciplineScore", Map.of("user", 92, "average", 75, "percentile", 90));
        comparison.put("savingsRate", Map.of("user", 25, "average", 18, "percentile", 80));
        comparison.put("emergencyReadiness", Map.of("user", 73, "average", 45, "percentile", 85));
        comparison.put("wealthBuilding", Map.of("user", 85, "average", 60, "percentile", 82));
        analysis.put("comparison", comparison);
        
        List<String> strengths = Arrays.asList(
            "Excellent spending discipline (top 10%)",
            "Strong emergency fund preparation (top 15%)",
            "Consistent saving habits (top 20%)"
        );
        analysis.put("strengths", strengths);
        
        List<String> improvements = Arrays.asList(
            "Consider increasing MMF allocation",
            "Explore additional income streams",
            "Review and optimize weekend spending"
        );
        analysis.put("improvements", improvements);
        
        return analysis;
    }
}
