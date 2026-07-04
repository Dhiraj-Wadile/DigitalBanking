package com.digitalbanking.repository;

import com.digitalbanking.entity.customer.Customer;
import com.digitalbanking.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByUser(User user);

    Optional<Customer> findByCustomerNumber(String customerNumber);

    Boolean existsByPanNumber(String panNumber);

    Boolean existsByAadhaarNumber(String aadhaarNumber);
}
