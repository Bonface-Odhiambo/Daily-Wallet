package com.dailywallet.service;

import com.dailywallet.model.entity.InterestRecord;
import com.dailywallet.model.entity.Transaction;
import com.dailywallet.model.entity.Wallet;
import com.dailywallet.model.enums.TransactionStatus;
import com.dailywallet.model.enums.TransactionType;
import com.dailywallet.repository.InterestRecordRepository;
import com.dailywallet.repository.TransactionRepository;
import com.dailywallet.repository.WalletRepository;
import com.dailywallet.util.ReferenceNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterestService {

    private final WalletRepository walletRepository;
    private final InterestRecordRepository interestRecordRepository;
    private final TransactionRepository transactionRepository;

    @Value("${app.wallet.interest-rate}")
    private double annualInterestRate;

    @Transactional
    public void calculateAndCreditDailyInterest() {
        List<Wallet> savingsWallets = walletRepository.findSavingsWalletsWithBalance();
        LocalDate today = LocalDate.now();

        log.info("Calculating interest for {} savings wallets", savingsWallets.size());

        for (Wallet wallet : savingsWallets) {
            if (interestRecordRepository.findByWalletIdAndCalculationDate(wallet.getId(), today).isPresent()) {
                log.debug("Interest already calculated for wallet {} on {}", wallet.getId(), today);
                continue;
            }

            BigDecimal dailyRate = BigDecimal.valueOf(annualInterestRate / 365);
            BigDecimal principalAmount = wallet.getBalance();
            BigDecimal interestAmount = principalAmount
                .multiply(dailyRate)
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);

            wallet.setBalance(wallet.getBalance().add(interestAmount));
            wallet.setTotalInterestEarned(wallet.getTotalInterestEarned().add(interestAmount));
            walletRepository.save(wallet);

            Transaction transaction = Transaction.builder()
                .user(wallet.getUser())
                .toWallet(wallet)
                .transactionType(TransactionType.INTEREST_CREDIT)
                .status(TransactionStatus.COMPLETED)
                .amount(interestAmount)
                .referenceNumber(ReferenceNumberGenerator.generateTransactionReference())
                .description("Daily interest credit")
                .completedAt(LocalDateTime.now())
                .build();
            transactionRepository.save(transaction);

            InterestRecord record = InterestRecord.builder()
                .wallet(wallet)
                .calculationDate(today)
                .principalAmount(principalAmount)
                .interestAmount(interestAmount)
                .dailyRate(dailyRate)
                .transaction(transaction)
                .build();
            interestRecordRepository.save(record);

            log.info("Interest credited to wallet {}: {} KES", wallet.getId(), interestAmount);
        }

        log.info("Daily interest calculation completed");
    }
}
