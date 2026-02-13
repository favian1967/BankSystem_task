package com.favian.bank_test_case.repository;

import com.favian.bank_test_case.entity.Card;
import com.favian.bank_test_case.entity.User;
import com.favian.bank_test_case.entity.enums.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    
    Page<Card> findByUser(User user, Pageable pageable);
    
    Page<Card> findByUserAndStatus(User user, CardStatus status, Pageable pageable);
    
    Page<Card> findByUserAndMaskedNumberContainingIgnoreCase(User user, String maskedNumber, Pageable pageable);
    
    Optional<Card> findByIdAndUser(Long id, User user);
}
