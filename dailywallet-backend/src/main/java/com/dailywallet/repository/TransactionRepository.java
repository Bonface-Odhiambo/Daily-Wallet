package com.dailywallet.repository;

import com.dailywallet.model.entity.Transaction;
import com.dailywallet.model.enums.TransactionStatus;
import com.dailywallet.model.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Page<Transaction> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    List<Transaction> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
        Long userId, LocalDateTime start, LocalDateTime end);
    
    Optional<Transaction> findByReferenceNumber(String referenceNumber);
    
    Optional<Transaction> findByMpesaReceiptNumber(String mpesaReceiptNumber);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId " +
           "AND t.transactionType = :type AND t.status = 'COMPLETED' " +
           "AND t.createdAt BETWEEN :start AND :end")
    Double sumAmountByUserAndTypeAndDateRange(
        Long userId, TransactionType type, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT t.category, SUM(t.amount) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.status = 'COMPLETED' " +
           "AND t.transactionType = 'WITHDRAWAL' " +
           "AND t.createdAt BETWEEN :start AND :end " +
           "GROUP BY t.category")
    List<Object[]> getSpendingByCategory(Long userId, LocalDateTime start, LocalDateTime end);
}
