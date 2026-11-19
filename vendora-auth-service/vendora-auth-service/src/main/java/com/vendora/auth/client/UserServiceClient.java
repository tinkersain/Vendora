package com.vendora.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.vendora.auth.dto.AuthUserDetails;

@FeignClient(name = "VENDORA-USER-SERVICE")
public interface UserServiceClient {

    @GetMapping("/internal/users/auth-details")
    AuthUserDetails getUserByEmail(
            @RequestParam("email") String email,
            @RequestHeader("X-Service-Secret") String serviceSecret);
}
