package com.dailywallet.scheduler;

import com.dailywallet.service.InterestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WalletScheduler {

    private final InterestService interestService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void calculateDailyInterest() {
        log.info("Starting daily interest calculation job");
        try {
            interestService.calculateAndCreditDailyInterest();
            log.info("Daily interest calculation completed successfully");
        } catch (Exception e) {
            log.error("Error calculating daily interest", e);
        }
    }

    @Scheduled(cron = "0 0 6 * * ?")
    public void releaseDailyWallets() {
        log.info("Daily wallet release job triggered at 6 AM");
    }

    @Scheduled(cron = "0 0 0 ? * SUN")
    public void releaseWeeklyWallets() {
        log.info("Weekly wallet release job triggered");
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void releaseMonthlyWallets() {
        log.info("Monthly wallet release job triggered");
    }
}
