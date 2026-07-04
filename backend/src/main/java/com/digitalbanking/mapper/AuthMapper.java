package com.digitalbanking.mapper;

import com.digitalbanking.entity.auth.User;
import com.digitalbanking.dto.auth.RegisterRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public User registerToUser(RegisterRequest request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPhone(request.getPhone());
        return user;
    }
}
