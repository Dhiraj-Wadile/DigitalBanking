package com.digitalbanking.service.beneficiary;

import com.digitalbanking.dto.beneficiary.*;
import com.digitalbanking.entity.beneficiary.Beneficiary;
import com.digitalbanking.entity.customer.Customer;
import com.digitalbanking.exception.ResourceNotFoundException;
import com.digitalbanking.repository.BeneficiaryRepository;
import com.digitalbanking.repository.CustomerRepository;
import com.digitalbanking.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final CustomerRepository customerRepository;
    private final SecurityUtils securityUtils;

    @Transactional
    public BeneficiaryResponse addBeneficiary(BeneficiaryRequest request) {
        Customer customer = getCustomer();

        Beneficiary beneficiary = Beneficiary.builder()
                .customer(customer)
                .name(request.getName())
                .accountNumber(request.getAccountNumber())
                .ifscCode(request.getIfscCode())
                .bankName(request.getBankName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .upiId(request.getUpiId())
                .type(Beneficiary.BeneficiaryType.valueOf(
                        request.getType() != null ? request.getType() : "BANK_TRANSFER"))
                .verified(false)
                .active(true)
                .build();

        beneficiary = beneficiaryRepository.save(beneficiary);
        log.info("Beneficiary added: {} for customer: {}", beneficiary.getName(), customer.getCustomerNumber());
        return mapToResponse(beneficiary);
    }

    public List<BeneficiaryResponse> getMyBeneficiaries() {
        Customer customer = getCustomer();
        return beneficiaryRepository.findByCustomerAndActiveTrue(customer).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void deleteBeneficiary(Long beneficiaryId) {
        Customer customer = getCustomer();
        Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found"));

        if (!beneficiary.getCustomer().getId().equals(customer.getId())) {
            throw new ResourceNotFoundException("Beneficiary not found");
        }

        beneficiary.setActive(false);
        beneficiaryRepository.save(beneficiary);
        log.info("Beneficiary deleted: {}", beneficiary.getName());
    }

    private Customer getCustomer() {
        Long userId = securityUtils.getCurrentUserId();
        return customerRepository.findByUser(
                new com.digitalbanking.entity.auth.User() {{ setId(userId); }})
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    private BeneficiaryResponse mapToResponse(Beneficiary b) {
        BeneficiaryResponse response = new BeneficiaryResponse();
        response.setId(b.getId());
        response.setName(b.getName());
        response.setAccountNumber(b.getAccountNumber());
        response.setIfscCode(b.getIfscCode());
        response.setBankName(b.getBankName());
        response.setPhone(b.getPhone());
        response.setType(b.getType().name());
        response.setVerified(b.getVerified());
        response.setActive(b.getActive());
        response.setUpiId(b.getUpiId());
        return response;
    }
}
