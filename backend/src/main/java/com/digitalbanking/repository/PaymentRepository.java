package com.digitalbanking.repository;

import com.digitalbanking.entity.payment.Payment;
import com.digitalbanking.entity.account.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentReference(String paymentReference);

    Page<Payment> findByAccountOrderByPaymentDateDesc(Account account, Pageable pageable);

    List<Payment> findByStatus(com.digitalbanking.entity.payment.Payment.PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.account.customer.id = :customerId ORDER BY p.paymentDate DESC")
    Page<Payment> findByCustomerId(@Param("customerId") Long customerId, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.scheduled = true AND p.status = 'SCHEDULED'")
    List<Payment> findScheduledPayments();
}
