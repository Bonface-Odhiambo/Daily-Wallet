package com.dailywallet.service;

import com.dailywallet.dto.request.ReallocateRequest;
import com.dailywallet.dto.request.SetAllocationRequest;
import com.dailywallet.dto.response.WalletResponse;
import com.dailywallet.exception.BusinessException;
import com.dailywallet.exception.ResourceNotFoundException;
import com.dailywallet.model.entity.AllocationRule;
import com.dailywallet.model.entity.Transaction;
import com.dailywallet.model.entity.User;
import com.dailywallet.model.entity.Wallet;
import com.dailywallet.model.enums.TransactionStatus;
import com.dailywallet.model.enums.TransactionType;
import com.dailywallet.model.enums.WalletType;
import com.dailywallet.repository.AllocationRuleRepository;
import com.dailywallet.repository.TransactionRepository;
import com.dailywallet.repository.UserRepository;
import com.dailywallet.repository.WalletRepository;
import com.dailywallet.util.ReferenceNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final AllocationRuleRepository allocationRuleRepository;
    private final TransactionRepository transactionRepository;

    public List<WalletResponse> getUserWallets(String phoneNumber) {
        User user = getUserByPhoneNumber(phoneNumber);
        List<Wallet> wallets = walletRepository.findByUserId(user.getId());
        
        return wallets.stream()
                .map(this::mapToWalletResponse)
                .collect(Collectors.toList());
    }

    public WalletResponse getWalletByType(String phoneNumber, WalletType walletType) {
        User user = getUserByPhoneNumber(phoneNumber);
        Wallet wallet = walletRepository.findByUserIdAndWalletType(user.getId(), walletType)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        
        return mapToWalletResponse(wallet);
    }

    @Transactional
    public void setAllocationRule(String phoneNumber, SetAllocationRequest request) {
        User user = getUserByPhoneNumber(phoneNumber);

        BigDecimal total = request.getDailyPercentage()
                .add(request.getWeeklyPercentage())
                .add(request.getMonthlyPercentage())
                .add(request.getSavingsPercentage());

        if (total.compareTo(new BigDecimal("100")) != 0) {
            throw new BusinessException("Allocation percentages must sum to 100");
        }

        AllocationRule rule = allocationRuleRepository.findByUserId(user.getId())
                .orElse(AllocationRule.builder().user(user).build());

        rule.setDailyPercentage(request.getDailyPercentage());
        rule.setWeeklyPercentage(request.getWeeklyPercentage());
        rule.setMonthlyPercentage(request.getMonthlyPercentage());
        rule.setSavingsPercentage(request.getSavingsPercentage());
        rule.setActive(true);

        allocationRuleRepository.save(rule);

        user.setOnboardingCompleted(true);
        userRepository.save(user);

        log.info("Allocation rule set for user: {}", phoneNumber);
    }

    @Transactional
    public void reallocateFunds(String phoneNumber, ReallocateRequest request) {
        User user = getUserByPhoneNumber(phoneNumber);

        if (request.getFromWalletType() == request.getToWalletType()) {
            throw new BusinessException("Cannot reallocate to the same wallet");
        }

        Wallet fromWallet = walletRepository.findByUserIdAndWalletType(user.getId(), request.getFromWalletType())
                .orElseThrow(() -> new ResourceNotFoundException("Source wallet not found"));

        Wallet toWallet = walletRepository.findByUserIdAndWalletType(user.getId(), request.getToWalletType())
                .orElseThrow(() -> new ResourceNotFoundException("Destination wallet not found"));

        if (fromWallet.getAvailableBalance().compareTo(request.getAmount()) < 0) {
            throw new BusinessException("Insufficient balance in source wallet");
        }

        fromWallet.setBalance(fromWallet.getBalance().subtract(request.getAmount()));
        toWallet.setBalance(toWallet.getBalance().add(request.getAmount()));

        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        Transaction transaction = Transaction.builder()
                .user(user)
                .fromWallet(fromWallet)
                .toWallet(toWallet)
                .transactionType(TransactionType.REALLOCATION)
                .status(TransactionStatus.COMPLETED)
                .amount(request.getAmount())
                .referenceNumber(ReferenceNumberGenerator.generateTransactionReference())
                .description("Reallocation from " + request.getFromWalletType() + " to " + request.getToWalletType())
                .completedAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

        log.info("Funds reallocated for user {}: {} from {} to {}", 
                phoneNumber, request.getAmount(), request.getFromWalletType(), request.getToWalletType());
    }

    @Transactional
    public void allocateDeposit(User user, BigDecimal amount) {
        AllocationRule rule = allocationRuleRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException("Allocation rule not set. Please complete onboarding."));

        List<Wallet> wallets = walletRepository.findByUserId(user.getId());

        for (Wallet wallet : wallets) {
            BigDecimal allocation = BigDecimal.ZERO;
            
            switch (wallet.getWalletType()) {
                case DAILY -> allocation = amount.multiply(rule.getDailyPercentage()).divide(new BigDecimal("100"));
                case WEEKLY -> allocation = amount.multiply(rule.getWeeklyPercentage()).divide(new BigDecimal("100"));
                case MONTHLY -> allocation = amount.multiply(rule.getMonthlyPercentage()).divide(new BigDecimal("100"));
                case SAVINGS -> allocation = amount.multiply(rule.getSavingsPercentage()).divide(new BigDecimal("100"));
            }

            wallet.setBalance(wallet.getBalance().add(allocation));
            wallet.setTotalDeposited(wallet.getTotalDeposited().add(allocation));
        }

        walletRepository.saveAll(wallets);
        log.info("Deposit allocated for user {}: {}", user.getPhoneNumber(), amount);
    }

    private User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private WalletResponse mapToWalletResponse(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .walletType(wallet.getWalletType())
                .balance(wallet.getBalance())
                .lockedBalance(wallet.getLockedBalance())
                .availableBalance(wallet.getAvailableBalance())
                .totalDeposited(wallet.getTotalDeposited())
                .totalWithdrawn(wallet.getTotalWithdrawn())
                .totalInterestEarned(wallet.getTotalInterestEarned())
                .lastReleaseAt(wallet.getLastReleaseAt())
                .nextReleaseAt(wallet.getNextReleaseAt())
                .build();
    }
}
