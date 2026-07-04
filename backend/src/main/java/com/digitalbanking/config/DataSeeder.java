package com.digitalbanking.config;

import com.digitalbanking.entity.auth.User;
import com.digitalbanking.entity.account.Branch;
import com.digitalbanking.entity.account.Account;
import com.digitalbanking.entity.customer.Customer;
import com.digitalbanking.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
@Profile("local")
@Slf4j
@SuppressWarnings("unused")
public class DataSeeder {

    @Bean
    CommandLineRunner initData(
            UserRepository userRepository,
            BranchRepository branchRepository,
            CustomerRepository customerRepository,
            AccountRepository accountRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (userRepository.count() > 0) {
                log.info("Database already seeded, skipping...");
                return;
            }

            log.info("Seeding demo data...");

            // Branches
            Branch mainBranch = branchRepository.save(Branch.builder()
                    .branchCode("BR001").branchName("Main Branch - Mumbai")
                    .address("123 Banking Street").city("Mumbai").state("Maharashtra")
                    .pincode("400001").phone("02212345678").email("main@digitalbanking.com")
                    .active(true).ifscCode("DBIN0001").build());

            Branch delhiBranch = branchRepository.save(Branch.builder()
                    .branchCode("BR002").branchName("Delhi Branch")
                    .address("456 Finance Road").city("New Delhi").state("Delhi")
                    .pincode("110001").phone("01112345678").email("delhi@digitalbanking.com")
                    .active(true).ifscCode("DBIN0002").build());

            Branch blrBranch = branchRepository.save(Branch.builder()
                    .branchCode("BR003").branchName("Bangalore Branch")
                    .address("789 Tech Park").city("Bangalore").state("Karnataka")
                    .pincode("560001").phone("08012345678").email("bangalore@digitalbanking.com")
                    .active(true).ifscCode("DBIN0003").build());

            // Admin User
            User adminUser = userRepository.save(User.builder()
                    .email("admin@digitalbanking.com").username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("System").lastName("Administrator")
                    .phone("9999999999").role(User.UserRole.ROLE_SUPER_ADMIN)
                    .enabled(true).emailVerified(true)
                    .accountLocked(false).mfaEnabled(false).build());

            // Employee User
            User empUser = userRepository.save(User.builder()
                    .email("employee@digitalbanking.com").username("employee")
                    .password(passwordEncoder.encode("employee123"))
                    .firstName("Test").lastName("Employee")
                    .phone("8888888888").role(User.UserRole.ROLE_EMPLOYEE)
                    .enabled(true).emailVerified(true)
                    .accountLocked(false).mfaEnabled(false).build());

            // Customer User
            User custUser = userRepository.save(User.builder()
                    .email("customer@digitalbanking.com").username("customer")
                    .password(passwordEncoder.encode("customer123"))
                    .firstName("Rajesh").lastName("Kumar")
                    .phone("7777777777").role(User.UserRole.ROLE_CUSTOMER)
                    .enabled(true).emailVerified(true)
                    .accountLocked(false).mfaEnabled(false).build());

            // Second Customer
            User custUser2 = userRepository.save(User.builder()
                    .email("priya@example.com").username("priya")
                    .password(passwordEncoder.encode("customer123"))
                    .firstName("Priya").lastName("Sharma")
                    .phone("6666666666").role(User.UserRole.ROLE_CUSTOMER)
                    .enabled(true).emailVerified(true)
                    .accountLocked(false).mfaEnabled(false).build());

            // Customer Profiles
            Customer customer1 = customerRepository.save(Customer.builder()
                    .user(custUser).customerNumber("CUST001")
                    .panNumber("ABCPK1234A")
                    .dateOfBirth(LocalDate.of(1990, 5, 15))
                    .gender(Customer.Gender.MALE)
                    .addressLine1("123 Customer Street").city("Mumbai")
                    .state("Maharashtra").pincode("400001").country("India")
                    .kycStatus(Customer.KycStatus.VERIFIED)
                    .status(Customer.CustomerStatus.ACTIVE)
                    .occupation("Software Engineer")
                    .annualIncome(1500000L)
                    .nomineeAdded(false).build());

            Customer customer2 = customerRepository.save(Customer.builder()
                    .user(custUser2).customerNumber("CUST002")
                    .panNumber("XYZPR5678B")
                    .dateOfBirth(LocalDate.of(1988, 3, 22))
                    .gender(Customer.Gender.FEMALE)
                    .addressLine1("456 Park Avenue").city("Delhi")
                    .state("Delhi").pincode("110001").country("India")
                    .kycStatus(Customer.KycStatus.VERIFIED)
                    .status(Customer.CustomerStatus.ACTIVE)
                    .occupation("Business Analyst")
                    .annualIncome(1200000L)
                    .nomineeAdded(false).build());

            // Accounts
            accountRepository.save(Account.builder()
                    .accountNumber("DB0000000001").customer(customer1)
                    .branch(mainBranch).accountType(Account.AccountType.SAVINGS)
                    .status(Account.AccountStatus.ACTIVE)
                    .balance(new BigDecimal("50000.00"))
                    .availableBalance(new BigDecimal("50000.00"))
                    .holdAmount(BigDecimal.ZERO)
                    .interestRate(new BigDecimal("4.00"))
                    .dailyTransactionLimit(new BigDecimal("500000"))
                    .singleTransactionLimit(new BigDecimal("100000"))
                    .dormant(false).overdraftEnabled(false)
                    .overdraftLimit(BigDecimal.ZERO)
                    .ifscCode("DBIN0001").openedDate(LocalDate.of(2024, 1, 1)).build());

            accountRepository.save(Account.builder()
                    .accountNumber("DB0000000002").customer(customer1)
                    .branch(mainBranch).accountType(Account.AccountType.CURRENT)
                    .status(Account.AccountStatus.ACTIVE)
                    .balance(new BigDecimal("150000.00"))
                    .availableBalance(new BigDecimal("150000.00"))
                    .holdAmount(BigDecimal.ZERO)
                    .interestRate(BigDecimal.ZERO)
                    .dailyTransactionLimit(new BigDecimal("500000"))
                    .singleTransactionLimit(new BigDecimal("100000"))
                    .dormant(false).overdraftEnabled(false)
                    .overdraftLimit(BigDecimal.ZERO)
                    .ifscCode("DBIN0001").openedDate(LocalDate.of(2024, 1, 15)).build());

            accountRepository.save(Account.builder()
                    .accountNumber("DB0000000003").customer(customer2)
                    .branch(delhiBranch).accountType(Account.AccountType.SAVINGS)
                    .status(Account.AccountStatus.ACTIVE)
                    .balance(new BigDecimal("75000.00"))
                    .availableBalance(new BigDecimal("75000.00"))
                    .holdAmount(BigDecimal.ZERO)
                    .interestRate(new BigDecimal("4.00"))
                    .dailyTransactionLimit(new BigDecimal("500000"))
                    .singleTransactionLimit(new BigDecimal("100000"))
                    .dormant(false).overdraftEnabled(false)
                    .overdraftLimit(BigDecimal.ZERO)
                    .ifscCode("DBIN0002").openedDate(LocalDate.of(2024, 2, 1)).build());

            log.info("=== Demo Data Seeded Successfully ===");
            log.info("Admin Login:    admin@digitalbanking.com / admin123");
            log.info("Employee Login: employee@digitalbanking.com / employee123");
            log.info("Customer Login: customer@digitalbanking.com / customer123");
            log.info("Customer Login: priya@example.com / customer123");
            log.info("H2 Console:     http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:digitalbanking)");
        };
    }
}
