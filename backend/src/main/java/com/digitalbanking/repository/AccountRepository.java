package com.digitalbanking.repository;

import com.digitalbanking.entity.account.Account;
import com.digitalbanking.entity.customer.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByCustomer(Customer customer);

    Page<Account> findByCustomer(Customer customer, Pageable pageable);

    @Query("SELECT a FROM Account a WHERE a.customer = :customer AND a.status = 'ACTIVE'")
    List<Account> findActiveAccountsByCustomer(@Param("customer") Customer customer);

    @Query("SELECT a FROM Account a WHERE a.customer = :customer AND a.accountType = :type")
    List<Account> findByCustomerAndType(@Param("customer") Customer customer, @Param("type") String type);

    Boolean existsByAccountNumber(String accountNumber);

    @Query("SELECT COUNT(a) FROM Account a WHERE a.status = :status")
    Long countByStatus(@Param("status") String status);
}
