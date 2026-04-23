package com.dailywallet.repository;

import com.dailywallet.model.entity.User;
import com.dailywallet.model.entity.Wallet;
import com.dailywallet.model.enums.WalletType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    
    List<Wallet> findByUserId(Long userId);
    
    List<Wallet> findByUser(User user);
    
    Optional<Wallet> findByUserIdAndWalletType(Long userId, WalletType walletType);
    
    Optional<Wallet> findByUserAndWalletType(User user, WalletType walletType);
    
    @Query("SELECT w FROM Wallet w WHERE w.walletType = :walletType AND w.nextReleaseAt <= :now")
    List<Wallet> findWalletsDueForRelease(WalletType walletType, LocalDateTime now);
    
    @Query("SELECT w FROM Wallet w WHERE w.walletType = 'SAVINGS' AND w.balance > 0")
    List<Wallet> findSavingsWalletsWithBalance();
}
