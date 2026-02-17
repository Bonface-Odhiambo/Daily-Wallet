package com.dailywallet.repository;

import com.dailywallet.model.entity.AllocationRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AllocationRuleRepository extends JpaRepository<AllocationRule, Long> {
    
    Optional<AllocationRule> findByUserId(Long userId);
}
