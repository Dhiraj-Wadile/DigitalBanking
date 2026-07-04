package com.digitalbanking.controller.customer;

import com.digitalbanking.dto.common.ApiResponse;
import com.digitalbanking.dto.customer.*;
import com.digitalbanking.entity.customer.Customer;
import com.digitalbanking.service.customer.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "Customer management endpoints")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/register")
    @Operation(summary = "Register new customer")
    public ResponseEntity<ApiResponse<Customer>> register(@RequestBody CustomerRegistrationRequest request) {
        Customer customer = customerService.registerCustomer(request);
        return ResponseEntity.ok(ApiResponse.success("Customer registered", customer));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get customer profile")
    public ResponseEntity<ApiResponse<Customer>> getProfile() {
        Customer customer = customerService.getCustomerProfile();
        return ResponseEntity.ok(ApiResponse.success(customer));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update customer profile")
    public ResponseEntity<ApiResponse<Customer>> updateProfile(@RequestBody UpdateProfileRequest request) {
        Customer customer = customerService.updateProfile(request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated", customer));
    }

    @GetMapping("/{customerNumber}")
    @Operation(summary = "Get customer by number")
    public ResponseEntity<ApiResponse<Customer>> getCustomerByNumber(@PathVariable String customerNumber) {
        Customer customer = customerService.getCustomerByNumber(customerNumber);
        return ResponseEntity.ok(ApiResponse.success(customer));
    }
}
