package com.dailywallet.repository;

import com.dailywallet.model.entity.MoneyMarketFund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MoneyMarketFundRepository extends JpaRepository<MoneyMarketFund, Long> {
    
    Optional<MoneyMarketFund> findByWalletId(Long walletId);
    
    Optional<MoneyMarketFund> findByUserId(Long userId);
    
    List<MoneyMarketFund> findByActiveTrue();
    
    @Query("SELECT mmf FROM MoneyMarketFund mmf WHERE mmf.active = true AND mmf.totalInvested > 0")
    List<MoneyMarketFund> findActiveInvestments();
    
    @Query("SELECT SUM(mmf.currentValue) FROM MoneyMarketFund mmf WHERE mmf.active = true")
    java.math.BigDecimal getTotalFundsUnderManagement();
}
