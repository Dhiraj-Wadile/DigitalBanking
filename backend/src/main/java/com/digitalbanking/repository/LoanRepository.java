package com.digitalbanking.repository;

import com.digitalbanking.entity.loan.Loan;
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
public interface LoanRepository extends JpaRepository<Loan, Long> {

    Optional<Loan> findByLoanAccountNumber(String loanAccountNumber);

    List<Loan> findByAccount(Account account);

    @Query("SELECT l FROM Loan l WHERE l.account.customer.id = :customerId")
    Page<Loan> findByCustomerId(Long customerId, Pageable pageable);

    @Query("SELECT l FROM Loan l WHERE l.status = :status")
    List<Loan> findByStatus(String status);
}
