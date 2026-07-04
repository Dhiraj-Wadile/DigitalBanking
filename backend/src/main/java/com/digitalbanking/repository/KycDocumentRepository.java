package com.digitalbanking.repository;

import com.digitalbanking.entity.kyc.KycDocument;
import com.digitalbanking.entity.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KycDocumentRepository extends JpaRepository<KycDocument, Long> {

    List<KycDocument> findByCustomer(Customer customer);

    List<KycDocument> findByStatus(String status);
}
