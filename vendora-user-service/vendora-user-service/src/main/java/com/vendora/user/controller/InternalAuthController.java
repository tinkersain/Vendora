package com.vendora.user.controller;

import com.vendora.user.dto.AuthUserDetailsResponse;
import com.vendora.user.service.UserService;
 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
 
@RestController
@RequestMapping("/internal/users")
public class InternalAuthController {
 
    private final UserService userService;
 
    @Value("${internal.service.secret}")
    private String serviceSecret;
 
    public InternalAuthController(UserService userService) {
        this.userService = userService;
    }
 
    @GetMapping("/auth-details")
    public AuthUserDetailsResponse getAuthDetails(
            @RequestParam String email,
            @RequestHeader("X-Service-Secret") String secret) {
 
        if (!serviceSecret.equals(secret)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Unauthorized service access");
        }
 
        return userService.getAuthUserDetails(email);
    }
}
 