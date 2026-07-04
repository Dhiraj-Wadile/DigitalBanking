package com.digitalbanking.repository;

import com.digitalbanking.entity.beneficiary.Beneficiary;
import com.digitalbanking.entity.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {

    List<Beneficiary> findByCustomerAndActiveTrue(Customer customer);

    Optional<Beneficiary> findByCustomerAndAccountNumber(Customer customer, String accountNumber);

    Optional<Beneficiary> findByCustomerAndUpiId(Customer customer, String upiId);
}
