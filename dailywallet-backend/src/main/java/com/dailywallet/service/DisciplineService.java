package com.dailywallet.service;

import com.dailywallet.dto.request.DailyAllowanceRequest;
import com.dailywallet.dto.request.ReallocateRequest;
import com.dailywallet.dto.response.DisciplineBucketResponse;
import com.dailywallet.dto.response.DisciplineScoreResponse;
import com.dailywallet.model.entity.User;
import com.dailywallet.model.entity.Wallet;
import com.dailywallet.model.enums.WalletType;
import com.dailywallet.repository.UserRepository;
import com.dailywallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DisciplineService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public List<DisciplineBucketResponse> getDisciplineBuckets(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Wallet> wallets = walletRepository.findByUser(user);
        
        return wallets.stream()
                .map(this::convertToDisciplineBucket)
                .collect(Collectors.toList());
    }

    public void setDailyAllowance(String phoneNumber, DailyAllowanceRequest request) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update daily wallet with allowance
        Wallet dailyWallet = walletRepository.findByUserAndWalletType(user, WalletType.DAILY)
                .orElseThrow(() -> new RuntimeException("Daily wallet not found"));

        dailyWallet.setBalance(request.getDailyAmount());
        dailyWallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(dailyWallet);

        log.info("Daily allowance set for user {}: KES {}", phoneNumber, request.getDailyAmount());
    }

    public DisciplineScoreResponse calculateFinancialHealthScore(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Wallet> wallets = walletRepository.findByUser(user);
        
        // Calculate discipline score based on wallet balances and patterns
        int disciplineScore = calculateDisciplineScore(wallets);
        int emergencyReadiness = calculateEmergencyReadiness(wallets);
        int wealthBuilding = calculateWealthBuilding(wallets);
        int spendingControl = calculateSpendingControl(wallets);
        int savingConsistency = calculateSavingConsistency(wallets);
        
        int overallScore = (disciplineScore + emergencyReadiness + wealthBuilding + spendingControl + savingConsistency) / 5;

        return DisciplineScoreResponse.builder()
                .overallScore(overallScore)
                .disciplineScore(disciplineScore)
                .emergencyReadiness(emergencyReadiness)
                .wealthBuilding(wealthBuilding)
                .spendingControl(spendingControl)
                .savingConsistency(savingConsistency)
                .grade(calculateGrade(overallScore))
                .recommendation(generateRecommendation(overallScore))
                .lastCalculated(LocalDateTime.now())
                .streakDays(14) // Mock streak data
                .achievementLevel(calculateAchievementLevel(overallScore))
                .financialHealthIndex(BigDecimal.valueOf(overallScore * 0.87))
                .onTrack(overallScore >= 70)
                .nextMilestone("Next milestone: Score 90")
                .daysToNextMilestone(7)
                .build();
    }

    public Object getDisciplineStreaks(String phoneNumber) {
        Map<String, Object> streaks = new HashMap<>();
        streaks.put("currentStreak", 14);
        streaks.put("longestStreak", 23);
        streaks.put("streakType", "Daily Spending Control");
        streaks.put("nextMilestone", 21);
        streaks.put("achievements", Arrays.asList("7-Day Warrior", "14-Day Champion"));
        streaks.put("lastActivity", LocalDateTime.now().minusDays(1));
        return streaks;
    }

    public void reallocateFunds(String phoneNumber, ReallocateRequest request) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet fromWallet = walletRepository.findByUserAndWalletType(user, request.getFromWalletType())
                .orElseThrow(() -> new RuntimeException("Source wallet not found"));
        
        Wallet toWallet = walletRepository.findByUserAndWalletType(user, request.getToWalletType())
                .orElseThrow(() -> new RuntimeException("Destination wallet not found"));

        if (fromWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance in source wallet");
        }

        fromWallet.setBalance(fromWallet.getBalance().subtract(request.getAmount()));
        fromWallet.setTotalWithdrawn(fromWallet.getTotalWithdrawn().add(request.getAmount()));
        
        toWallet.setBalance(toWallet.getBalance().add(request.getAmount()));
        toWallet.setTotalDeposited(toWallet.getTotalDeposited().add(request.getAmount()));

        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        log.info("Funds reallocated: {} from {} to {}", request.getAmount(), request.getFromWalletType(), request.getToWalletType());
    }

    public Object getSpendingInsights(String phoneNumber) {
        Map<String, Object> insights = new HashMap<>();
        insights.put("averageDailySpending", 450);
        insights.put("spendingTrend", "decreasing");
        insights.put("topCategory", "Food & Groceries");
        insights.put("budgetUtilization", 75);
        insights.put("savingsRate", 25);
        insights.put("recommendations", Arrays.asList("Reduce weekend spending", "Increase emergency fund"));
        return insights;
    }

    public Object getEmergencyProgress(String phoneNumber) {
        Map<String, Object> progress = new HashMap<>();
        progress.put("targetAmount", 3000);
        progress.put("currentAmount", 2190);
        progress.put("progressPercentage", 73);
        progress.put("monthlyContribution", 600);
        progress.put("monthsToComplete", 2);
        progress.put("onTrack", true);
        return progress;
    }

    public void actOnNudge(String phoneNumber, String nudgeId, Object actionRequest) {
        log.info("User {} acted on nudge {}: {}", phoneNumber, nudgeId, actionRequest);
        // Implementation for tracking nudge actions
    }

    private DisciplineBucketResponse convertToDisciplineBucket(Wallet wallet) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("walletType", wallet.getWalletType().name());
        
        return DisciplineBucketResponse.builder()
                .id(wallet.getId())
                .bucketType(mapWalletTypeToBucket(wallet.getWalletType()))
                .bucketName(getBucketName(wallet.getWalletType()))
                .balance(wallet.getBalance())
                .availableBalance(wallet.getAvailableBalance())
                .lockedBalance(wallet.getLockedBalance())
                .totalDeposited(wallet.getTotalDeposited())
                .totalWithdrawn(wallet.getTotalWithdrawn())
                .totalInterestEarned(wallet.getTotalInterestEarned())
                .lastReleaseAt(wallet.getLastReleaseAt())
                .nextReleaseAt(wallet.getNextReleaseAt())
                .progressPercentage(calculateProgressPercentage(wallet))
                .status(getBucketStatus(wallet))
                .metadata(metadata)
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .isProtected(wallet.getWalletType() == WalletType.SAVINGS)
                .description(getBucketDescription(wallet.getWalletType()))
                .build();
    }

    private String mapWalletTypeToBucket(WalletType walletType) {
        switch (walletType) {
            case DAILY: return "SPENDING_WALLET";
            case SAVINGS: return "EMERGENCY_SAVINGS";
            case WEEKLY: return "MMF_GROWTH";
            case MONTHLY: return "OVERDRAFT_FACILITY";
            default: return walletType.name();
        }
    }

    private String getBucketName(WalletType walletType) {
        switch (walletType) {
            case DAILY: return "Spending Wallet";
            case SAVINGS: return "Emergency Savings";
            case WEEKLY: return "MMF Growth Bucket";
            case MONTHLY: return "Overdraft Facility";
            default: return walletType.name();
        }
    }

    private String getBucketDescription(WalletType walletType) {
        switch (walletType) {
            case DAILY: return "Daily allowance for controlled spending";
            case SAVINGS: return "Protected emergency fund for financial resilience";
            case WEEKLY: return "Money market fund for wealth building";
            case MONTHLY: return "Responsible overdraft facility";
            default: return "Wallet";
        }
    }

    private BigDecimal calculateProgressPercentage(Wallet wallet) {
        if (wallet.getTotalDeposited().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return wallet.getBalance()
                .divide(wallet.getTotalDeposited(), 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private String getBucketStatus(Wallet wallet) {
        if (wallet.getBalance().compareTo(BigDecimal.ZERO) == 0) {
            return "EMPTY";
        } else if (wallet.getBalance().compareTo(wallet.getTotalDeposited().multiply(BigDecimal.valueOf(0.8))) > 0) {
            return "HEALTHY";
        } else {
            return "LOW";
        }
    }

    private int calculateDisciplineScore(List<Wallet> wallets) {
        // Mock calculation based on wallet balances and patterns
        return 92;
    }

    private int calculateEmergencyReadiness(List<Wallet> wallets) {
        // Mock calculation based on emergency savings
        return 73;
    }

    private int calculateWealthBuilding(List<Wallet> wallets) {
        // Mock calculation based on MMF and savings
        return 85;
    }

    private int calculateSpendingControl(List<Wallet> wallets) {
        // Mock calculation based on daily spending patterns
        return 88;
    }

    private int calculateSavingConsistency(List<Wallet> wallets) {
        // Mock calculation based on regular savings
        return 90;
    }

    private String calculateGrade(int score) {
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "E";
    }

    private String generateRecommendation(int score) {
        if (score >= 80) return "Excellent financial discipline! Keep up the great work.";
        if (score >= 60) return "Good progress. Focus on increasing emergency savings.";
        return "Focus on building consistent saving habits and controlling daily spending.";
    }

    private String calculateAchievementLevel(int score) {
        if (score >= 90) return "Financial Champion";
        if (score >= 75) return "Discipline Master";
        if (score >= 60) return "Building Habits";
        return "Getting Started";
    }
}
