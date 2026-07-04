package com.digitalbanking.service.customer;

import com.digitalbanking.dto.customer.*;
import com.digitalbanking.entity.auth.User;
import com.digitalbanking.entity.customer.Customer;
import com.digitalbanking.exception.BadRequestException;
import com.digitalbanking.exception.ResourceNotFoundException;
import com.digitalbanking.repository.CustomerRepository;
import com.digitalbanking.repository.UserRepository;
import com.digitalbanking.security.SecurityUtils;
import com.digitalbanking.util.ReferenceGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Customer registerCustomer(CustomerRegistrationRequest request) {
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .username(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(User.UserRole.ROLE_CUSTOMER)
                .enabled(true)
                .build();

        user = userRepository.save(user);

        Customer customer = Customer.builder()
                .user(user)
                .customerNumber(ReferenceGenerator.generateCustomerNumber())
                .dateOfBirth(request.getDateOfBirth())
                .panNumber(request.getPanNumber())
                .aadhaarNumber(request.getAadhaarNumber())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .country(request.getCountry())
                .build();

        customer = customerRepository.save(customer);
        log.info("Customer registered successfully: {}", customer.getCustomerNumber());
        return customer;
    }

    public Customer getCustomerProfile() {
        Long userId = securityUtils.getCurrentUserId();
        return customerRepository.findByUser(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")))
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));
    }

    @Transactional
    public Customer updateProfile(UpdateProfileRequest request) {
        Customer customer = getCustomerProfile();
        User user = customer.getUser();

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());

        if (request.getAddressLine1() != null) customer.setAddressLine1(request.getAddressLine1());
        if (request.getAddressLine2() != null) customer.setAddressLine2(request.getAddressLine2());
        if (request.getCity() != null) customer.setCity(request.getCity());
        if (request.getState() != null) customer.setState(request.getState());
        if (request.getPincode() != null) customer.setPincode(request.getPincode());
        if (request.getCountry() != null) customer.setCountry(request.getCountry());
        if (request.getOccupation() != null) customer.setOccupation(request.getOccupation());

        userRepository.save(user);
        customer = customerRepository.save(customer);
        log.info("Customer profile updated: {}", customer.getCustomerNumber());
        return customer;
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    public Customer getCustomerByNumber(String customerNumber) {
        return customerRepository.findByCustomerNumber(customerNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with number: " + customerNumber));
    }
}
