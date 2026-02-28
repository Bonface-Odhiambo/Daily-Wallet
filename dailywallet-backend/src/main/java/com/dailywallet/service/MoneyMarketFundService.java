package com.dailywallet.service;

import com.dailywallet.exception.BusinessException;
import com.dailywallet.model.entity.MoneyMarketFund;
import com.dailywallet.model.entity.Transaction;
import com.dailywallet.model.entity.User;
import com.dailywallet.model.entity.Wallet;
import com.dailywallet.model.enums.TransactionStatus;
import com.dailywallet.model.enums.TransactionType;
import com.dailywallet.model.enums.WalletType;
import com.dailywallet.repository.MoneyMarketFundRepository;
import com.dailywallet.repository.TransactionRepository;
import com.dailywallet.repository.UserRepository;
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

/**
 * Service for managing Money Market Fund investments
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MoneyMarketFundService {

    private final MoneyMarketFundRepository mmfRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Value("${app.mmf.provider:CIC Money Market Fund}")
    private String defaultMmfProvider;

    @Value("${app.mmf.annual-return-rate:13.0}")
    private double defaultAnnualReturnRate;

    @Value("${app.mmf.auto-invest-threshold:1000.0}")
    private double autoInvestThreshold;

    /**
     * Initialize MMF account for a user's savings wallet
     */
    @Transactional
    public MoneyMarketFund initializeMMFAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        Wallet savingsWallet = walletRepository.findByUserIdAndWalletType(userId, WalletType.SAVINGS)
                .orElseThrow(() -> new BusinessException("Savings wallet not found"));

        if (mmfRepository.findByWalletId(savingsWallet.getId()).isPresent()) {
            throw new BusinessException("MMF account already exists for this wallet");
        }

        String mmfAccountNumber = generateMMFAccountNumber(userId);

        MoneyMarketFund mmf = MoneyMarketFund.builder()
                .user(user)
                .wallet(savingsWallet)
                .mmfProvider(defaultMmfProvider)
                .mmfAccountNumber(mmfAccountNumber)
                .annualReturnRate(BigDecimal.valueOf(defaultAnnualReturnRate))
                .currentNav(BigDecimal.ONE)
                .active(true)
                .build();

        mmf = mmfRepository.save(mmf);

        log.info("🏦 MMF account initialized for user {} with provider {}", userId, defaultMmfProvider);

        return mmf;
    }

    /**
     * Invest amount from savings wallet into MMF
     */
    @Transactional
    public void investInMMF(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Investment amount must be positive");
        }

        MoneyMarketFund mmf = mmfRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("MMF account not found. Please initialize first."));

        Wallet savingsWallet = mmf.getWallet();

        if (savingsWallet.getAvailableBalance().compareTo(amount) < 0) {
            throw new BusinessException("Insufficient balance in savings wallet");
        }

        // Calculate units to purchase based on current NAV
        BigDecimal units = amount.divide(mmf.getCurrentNav(), 6, RoundingMode.HALF_UP);

        // Update MMF
        mmf.setTotalInvested(mmf.getTotalInvested().add(amount));
        mmf.setTotalUnits(mmf.getTotalUnits().add(units));
        mmf.setCurrentValue(mmf.getCurrentValue().add(amount));
        mmf.setLastInvestmentDate(LocalDateTime.now());
        mmfRepository.save(mmf);

        // Create transaction record
        Transaction transaction = Transaction.builder()
                .user(mmf.getUser())
                .fromWallet(savingsWallet)
                .transactionType(TransactionType.MMF_INVESTMENT)
                .status(TransactionStatus.COMPLETED)
                .amount(amount)
                .referenceNumber(ReferenceNumberGenerator.generateTransactionReference())
                .description("Investment in " + mmf.getMmfProvider())
                .completedAt(LocalDateTime.now())
                .build();
        transactionRepository.save(transaction);

        log.info("💰 Invested KES {} into MMF for user {}. Units purchased: {}", amount, userId, units);
    }

    /**
     * Redeem (withdraw) amount from MMF back to savings wallet
     */
    @Transactional
    public void redeemFromMMF(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Redemption amount must be positive");
        }

        MoneyMarketFund mmf = mmfRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("MMF account not found"));

        if (mmf.getCurrentValue().compareTo(amount) < 0) {
            throw new BusinessException("Insufficient MMF balance");
        }

        // Calculate units to redeem
        BigDecimal unitsToRedeem = amount.divide(mmf.getCurrentNav(), 6, RoundingMode.HALF_UP);

        // Update MMF
        mmf.setTotalUnits(mmf.getTotalUnits().subtract(unitsToRedeem));
        mmf.setCurrentValue(mmf.getCurrentValue().subtract(amount));
        mmf.setLastRedemptionDate(LocalDateTime.now());
        mmfRepository.save(mmf);

        // Credit savings wallet
        Wallet savingsWallet = mmf.getWallet();
        savingsWallet.setBalance(savingsWallet.getBalance().add(amount));
        walletRepository.save(savingsWallet);

        // Create transaction record
        Transaction transaction = Transaction.builder()
                .user(mmf.getUser())
                .toWallet(savingsWallet)
                .transactionType(TransactionType.MMF_REDEMPTION)
                .status(TransactionStatus.COMPLETED)
                .amount(amount)
                .referenceNumber(ReferenceNumberGenerator.generateTransactionReference())
                .description("Redemption from " + mmf.getMmfProvider())
                .completedAt(LocalDateTime.now())
                .build();
        transactionRepository.save(transaction);

        log.info("💸 Redeemed KES {} from MMF for user {}. Units redeemed: {}", amount, userId, unitsToRedeem);
    }

    /**
     * Update NAV and calculate returns for all active MMF accounts
     * This should run daily
     */
    @Transactional
    public void updateDailyNavAndReturns() {
        List<MoneyMarketFund> activeMMFs = mmfRepository.findActiveInvestments();
        LocalDate today = LocalDate.now();

        log.info("📊 Updating NAV and returns for {} MMF accounts", activeMMFs.size());

        for (MoneyMarketFund mmf : activeMMFs) {
            // Calculate daily return rate
            BigDecimal dailyRate = mmf.getAnnualReturnRate()
                    .divide(BigDecimal.valueOf(365), 6, RoundingMode.HALF_UP)
                    .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);

            // Calculate daily return amount
            BigDecimal dailyReturn = mmf.getCurrentValue().multiply(dailyRate);

            // Update current value
            mmf.setCurrentValue(mmf.getCurrentValue().add(dailyReturn));
            mmf.setTotalReturns(mmf.getTotalReturns().add(dailyReturn));

            // Update NAV (simplified - in reality, NAV is provided by MMF provider)
            BigDecimal newNav = mmf.getCurrentValue().divide(mmf.getTotalUnits(), 6, RoundingMode.HALF_UP);
            mmf.setCurrentNav(newNav);
            mmf.setLastNavUpdate(today);

            mmfRepository.save(mmf);

            // Also update wallet's interest earned
            Wallet savingsWallet = mmf.getWallet();
            savingsWallet.setTotalInterestEarned(savingsWallet.getTotalInterestEarned().add(dailyReturn));
            walletRepository.save(savingsWallet);

            // Create interest transaction
            Transaction transaction = Transaction.builder()
                    .user(mmf.getUser())
                    .toWallet(savingsWallet)
                    .transactionType(TransactionType.INTEREST_CREDIT)
                    .status(TransactionStatus.COMPLETED)
                    .amount(dailyReturn)
                    .referenceNumber(ReferenceNumberGenerator.generateTransactionReference())
                    .description("MMF daily returns")
                    .completedAt(LocalDateTime.now())
                    .build();
            transactionRepository.save(transaction);

            log.info("✅ MMF returns credited for user {}: KES {}", mmf.getUser().getId(), dailyReturn);
        }

        log.info("📊 NAV update completed for {} MMF accounts", activeMMFs.size());
    }

    /**
     * Get MMF account details for a user
     */
    public MoneyMarketFund getMMFAccount(Long userId) {
        return mmfRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("MMF account not found"));
    }

    /**
     * Generate unique MMF account number
     */
    private String generateMMFAccountNumber(Long userId) {
        return "MMF" + String.format("%010d", userId) + System.currentTimeMillis() % 10000;
    }

    /**
     * Get total funds under management across all MMF accounts
     */
    public BigDecimal getTotalFundsUnderManagement() {
        BigDecimal total = mmfRepository.getTotalFundsUnderManagement();
        return total != null ? total : BigDecimal.ZERO;
    }
}
