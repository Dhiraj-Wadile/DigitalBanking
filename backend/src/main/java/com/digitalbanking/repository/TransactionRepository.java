package com.digitalbanking.repository;

import com.digitalbanking.entity.transaction.Transaction;
import com.digitalbanking.entity.account.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByReferenceNumber(String referenceNumber);

    Page<Transaction> findByAccountOrderByTransactionDateDesc(Account account, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.account = :account AND t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC")
    List<Transaction> findByAccountAndDateRange(
            @Param("account") Account account,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t FROM Transaction t WHERE t.account.customer.id = :customerId ORDER BY t.transactionDate DESC")
    Page<Transaction> findByCustomerId(@Param("customerId") Long customerId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.status = :status ORDER BY t.transactionDate DESC")
    List<Transaction> findByStatus(@Param("status") String status);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.account = :account AND t.transactionType = :type AND t.status = 'COMPLETED'")
    Optional<java.math.BigDecimal> sumAmountByAccountAndType(
            @Param("account") Account account,
            @Param("type") String type);

    Boolean existsByIdempotencyKey(String idempotencyKey);
}
