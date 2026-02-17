package com.dailywallet.repository;

import com.dailywallet.model.entity.InterestRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterestRecordRepository extends JpaRepository<InterestRecord, Long> {
    
    Optional<InterestRecord> findByWalletIdAndCalculationDate(Long walletId, LocalDate date);
    
    List<InterestRecord> findByWalletIdOrderByCalculationDateDesc(Long walletId);
    
    @Query("SELECT SUM(ir.interestAmount) FROM InterestRecord ir " +
           "WHERE ir.wallet.id = :walletId AND ir.calculationDate BETWEEN :start AND :end")
    Double sumInterestByWalletAndDateRange(Long walletId, LocalDate start, LocalDate end);
}
