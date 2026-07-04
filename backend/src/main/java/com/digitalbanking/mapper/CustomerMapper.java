package com.digitalbanking.mapper;

import com.digitalbanking.entity.customer.Customer;
import com.digitalbanking.entity.auth.User;
import com.digitalbanking.dto.customer.CustomerRegistrationRequest;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer registrationToCustomer(CustomerRegistrationRequest request, User user) {
        Customer customer = new Customer();
        customer.setUser(user);
        customer.setAddressLine1(request.getAddressLine1());
        customer.setAddressLine2(request.getAddressLine2());
        customer.setCity(request.getCity());
        customer.setState(request.getState());
        customer.setPincode(request.getPincode());
        customer.setCountry(request.getCountry());
        customer.setPanNumber(request.getPanNumber());
        customer.setAadhaarNumber(request.getAadhaarNumber());
        return customer;
    }
}
