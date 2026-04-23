package com.dailywallet.service;

import com.dailywallet.dto.request.MMFSweepRequest;
import com.dailywallet.dto.response.MMFInvestmentResponse;
import com.dailywallet.model.entity.User;
import com.dailywallet.model.entity.Wallet;
import com.dailywallet.model.enums.WalletType;
import com.dailywallet.repository.UserRepository;
import com.dailywallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MMFService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public List<MMFInvestmentResponse> getMMFInvestments(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Mock MMF investment data
        List<MMFInvestmentResponse> investments = new ArrayList<>();
        
        MMFInvestmentResponse investment = MMFInvestmentResponse.builder()
                .id(1L)
                .investedAmount(BigDecimal.valueOf(15000))
                .currentValue(BigDecimal.valueOf(15675))
                .totalYield(BigDecimal.valueOf(675))
                .yieldRate(BigDecimal.valueOf(1.2))
                .annualizedReturn(BigDecimal.valueOf(15.6))
                .mmfProvider("Cyber Capital")
                .fundName("Cyber Capital Money Market Fund")
                .investmentDate(LocalDateTime.now().minusMonths(3))
                .lastUpdated(LocalDateTime.now())
                .autoSweepEnabled(true)
                .sweepAmount(BigDecimal.valueOf(500))
                .sweepFrequency("daily")
                .managementFee(BigDecimal.valueOf(30))
                .netYield(BigDecimal.valueOf(645))
                .daysInvested(90)
                .dailyYield(BigDecimal.valueOf(7.5))
                .performanceMetrics(createPerformanceMetrics())
                .status("ACTIVE")
                .currency("KES")
                .compoundInterest(true)
                .targetAmount(BigDecimal.valueOf(20000))
                .progressPercentage(BigDecimal.valueOf(78.38))
                .isProtected(true)
                .build();
        
        investments.add(investment);
        return investments;
    }

    public Object executeSweep(String phoneNumber, MMFSweepRequest request) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("Executing MMF sweep for user {}: amount {}", phoneNumber, request.getAmount());

        Map<String, Object> result = new HashMap<>();
        
        BigDecimal sweepAmount = request.getAmount() != null ? request.getAmount() : BigDecimal.valueOf(500);
        
        result.put("sweepAmount", sweepAmount);
        result.put("sweepTime", LocalDateTime.now());
        result.put("autoSweep", request.getAutoSweep());
        result.put("frequency", request.getSweepFrequency());
        result.put("mmfProvider", "Cyber Capital");
        result.put("expectedYield", sweepAmount.multiply(BigDecimal.valueOf(0.012))); // 1.2% monthly
        result.put("status", "COMPLETED");
        result.put("transactionId", "MMF" + System.currentTimeMillis());
        
        return result;
    }

    public Object getYieldHistory(String phoneNumber, String period) {
        List<Map<String, Object>> history = new ArrayList<>();
        
        // Mock yield history data
        LocalDateTime startDate = LocalDateTime.now().minusMonths(6);
        for (int i = 0; i < 6; i++) {
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("period", startDate.plusMonths(i).getMonth().toString());
            monthData.put("yieldRate", BigDecimal.valueOf(1.2 + (i % 3) * 0.1));
            monthData.put("yieldAmount", BigDecimal.valueOf(600 + (i * 50)));
            monthData.put("investedAmount", BigDecimal.valueOf(15000 + (i * 1000)));
            monthData.put("totalValue", BigDecimal.valueOf(15675 + (i * 200)));
            history.add(monthData);
        }
        
        return history;
    }

    public Object getPerformanceMetrics(String phoneNumber) {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalInvested", 15000);
        metrics.put("currentValue", 15675);
        metrics.put("totalReturn", 675);
        metrics.put("returnPercentage", 4.5);
        metrics.put("annualizedReturn", 15.6);
        metrics.put("monthlyAverage", 112.5);
        metrics.put("bestMonth", "March 2024");
        metrics.put("bestMonthReturn", 8.2);
        metrics.put("worstMonth", "January 2024");
        metrics.put("worstMonthReturn", 0.8);
        metrics.put("volatility", "Low");
        metrics.put("riskRating", "AA");
        return metrics;
    }

    public void toggleAutoSweep(String phoneNumber, boolean enabled) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("Auto-sweep {} for user {}", enabled ? "enabled" : "disabled", phoneNumber);
        
        // Update user's auto-sweep preference
        // This would be stored in user preferences or settings
    }

    public Object getSweepPreview(String phoneNumber) {
        Map<String, Object> preview = new HashMap<>();
        preview.put("nextSweepAmount", 450);
        preview.put("nextSweepTime", LocalDateTime.now().plusHours(6));
        preview.put("availableForSweep", 1250);
        preview.put("emergencyFundProtected", 1000);
        preview.put("expectedMonthlyYield", 54);
        preview.put("sweepFrequency", "daily");
        preview.put("autoSweepEnabled", true);
        preview.put("mmfProvider", "Cyber Capital");
        return preview;
    }

    private Map<String, Object> createPerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("dailyYield", 7.5);
        metrics.put("weeklyYield", 52.5);
        metrics.put("monthlyYield", 225);
        metrics.put("ytdYield", 675);
        metrics.put("riskLevel", "LOW");
        metrics.put("volatilityIndex", 0.15);
        metrics.put("sharpeRatio", 2.8);
        metrics.put("maxDrawdown", -2.1);
        return metrics;
    }
}
