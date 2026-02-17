package com.dailywallet.service;

import com.dailywallet.dto.request.DepositRequest;
import com.dailywallet.dto.request.WithdrawRequest;
import com.dailywallet.dto.response.TransactionResponse;
import com.dailywallet.exception.BusinessException;
import com.dailywallet.exception.ResourceNotFoundException;
import com.dailywallet.model.entity.Transaction;
import com.dailywallet.model.entity.User;
import com.dailywallet.model.entity.Wallet;
import com.dailywallet.model.enums.TransactionStatus;
import com.dailywallet.model.enums.TransactionType;
import com.dailywallet.repository.TransactionRepository;
import com.dailywallet.repository.UserRepository;
import com.dailywallet.repository.WalletRepository;
import com.dailywallet.util.ReferenceNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final WalletService walletService;
    private final MpesaService mpesaService;

    @Transactional
    public TransactionResponse initiateDeposit(String phoneNumber, DepositRequest request) {
        User user = getUserByPhoneNumber(phoneNumber);

        Transaction transaction = Transaction.builder()
                .user(user)
                .transactionType(TransactionType.DEPOSIT)
                .status(TransactionStatus.PENDING)
                .amount(request.getAmount())
                .referenceNumber(ReferenceNumberGenerator.generateTransactionReference())
                .description("M-Pesa deposit")
                .build();

        transaction = transactionRepository.save(transaction);

        try {
            mpesaService.initiateStkPush(request.getPhoneNumber(), request.getAmount(), transaction.getReferenceNumber());
            log.info("STK push initiated for transaction: {}", transaction.getReferenceNumber());
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new BusinessException("Failed to initiate M-Pesa payment: " + e.getMessage());
        }

        return mapToTransactionResponse(transaction);
    }

    @Transactional
    public void completeDeposit(String referenceNumber, String mpesaReceiptNumber) {
        Transaction transaction = transactionRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (transaction.getStatus() != TransactionStatus.PENDING) {
            log.warn("Transaction {} is not pending. Current status: {}", referenceNumber, transaction.getStatus());
            return;
        }

        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setMpesaReceiptNumber(mpesaReceiptNumber);
        transaction.setCompletedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        walletService.allocateDeposit(transaction.getUser(), transaction.getAmount());

        log.info("Deposit completed: {} - {}", referenceNumber, transaction.getAmount());
    }

    @Transactional
    public TransactionResponse initiateWithdrawal(String phoneNumber, WithdrawRequest request) {
        User user = getUserByPhoneNumber(phoneNumber);

        Wallet wallet = walletRepository.findByUserIdAndWalletType(user.getId(), request.getWalletType())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        if (wallet.getAvailableBalance().compareTo(request.getAmount()) < 0) {
            throw new BusinessException("Insufficient balance in " + request.getWalletType() + " wallet");
        }

        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        wallet.setTotalWithdrawn(wallet.getTotalWithdrawn().add(request.getAmount()));
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .user(user)
                .fromWallet(wallet)
                .transactionType(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.PENDING)
                .amount(request.getAmount())
                .referenceNumber(ReferenceNumberGenerator.generateTransactionReference())
                .recipientPhoneNumber(request.getRecipientPhoneNumber())
                .description("Withdrawal to M-Pesa")
                .build();

        transaction = transactionRepository.save(transaction);

        try {
            mpesaService.initiateB2C(request.getRecipientPhoneNumber(), request.getAmount(), transaction.getReferenceNumber());
            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction.setCompletedAt(LocalDateTime.now());
            transactionRepository.save(transaction);
            log.info("Withdrawal initiated for transaction: {}", transaction.getReferenceNumber());
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            
            wallet.setBalance(wallet.getBalance().add(request.getAmount()));
            wallet.setTotalWithdrawn(wallet.getTotalWithdrawn().subtract(request.getAmount()));
            walletRepository.save(wallet);
            
            throw new BusinessException("Failed to process withdrawal: " + e.getMessage());
        }

        return mapToTransactionResponse(transaction);
    }

    public Page<TransactionResponse> getUserTransactions(String phoneNumber, Pageable pageable) {
        User user = getUserByPhoneNumber(phoneNumber);
        Page<Transaction> transactions = transactionRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
        return transactions.map(this::mapToTransactionResponse);
    }

    public List<TransactionResponse> getRecentTransactions(String phoneNumber, int limit) {
        User user = getUserByPhoneNumber(phoneNumber);
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        List<Transaction> transactions = transactionRepository
                .findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(user.getId(), startDate, LocalDateTime.now());
        
        return transactions.stream()
                .limit(limit)
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    private User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .transactionType(transaction.getTransactionType())
                .category(transaction.getCategory())
                .status(transaction.getStatus())
                .amount(transaction.getAmount())
                .fee(transaction.getFee())
                .referenceNumber(transaction.getReferenceNumber())
                .mpesaReceiptNumber(transaction.getMpesaReceiptNumber())
                .recipientPhoneNumber(transaction.getRecipientPhoneNumber())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .completedAt(transaction.getCompletedAt())
                .build();
    }
}
