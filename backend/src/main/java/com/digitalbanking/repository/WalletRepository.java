package com.digitalbanking.repository;

import com.digitalbanking.entity.customer.Wallet;
import com.digitalbanking.entity.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByWalletNumber(String walletNumber);

    Optional<Wallet> findByCustomerAndStatus(Customer customer, String status);
}
