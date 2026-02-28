package com.dailywallet.repository;

import com.dailywallet.model.entity.OverdraftAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OverdraftAccountRepository extends JpaRepository<OverdraftAccount, Long> {
    
    Optional<OverdraftAccount> findByUserId(Long userId);
    
    List<OverdraftAccount> findByActiveTrue();
    
    @Query("SELECT oa FROM OverdraftAccount oa WHERE oa.active = true AND oa.currentOverdraft > 0")
    List<OverdraftAccount> findAccountsInOverdraft();
    
    @Query("SELECT SUM(oa.currentOverdraft) FROM OverdraftAccount oa WHERE oa.active = true")
    java.math.BigDecimal getTotalOverdraftOutstanding();
}
