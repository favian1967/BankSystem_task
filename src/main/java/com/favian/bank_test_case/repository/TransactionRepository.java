package com.favian.bank_test_case.repository;

import com.favian.bank_test_case.entity.Card;
import com.favian.bank_test_case.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    @Query("SELECT t FROM Transaction t WHERE t.fromCard = :card OR t.toCard = :card")
    Page<Transaction> findByCard(@Param("card") Card card, Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE t.fromCard.user.id = :userId OR t.toCard.user.id = :userId")
    Page<Transaction> findByUserId(@Param("userId") Long userId, Pageable pageable);
}
