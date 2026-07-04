package com.digitalbanking.repository;

import com.digitalbanking.entity.card.Card;
import com.digitalbanking.entity.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardNumber(String cardNumber);

    List<Card> findByAccount(Account account);

    @Query("SELECT c FROM Card c WHERE c.account.customer.id = :customerId")
    List<Card> findByCustomerId(Long customerId);

    @Query("SELECT c FROM Card c WHERE c.status = :status")
    List<Card> findByStatus(String status);
}
