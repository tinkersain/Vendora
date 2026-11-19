package com.vendora.auth.service;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.vendora.auth.client.UserServiceClient;
import com.vendora.auth.dto.AuthUserDetails;
import com.vendora.auth.dto.LoginRequest;
import com.vendora.auth.dto.LoginResponse;
import com.vendora.auth.dto.TokenValidationResponse;

import feign.FeignException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

@Service
public class AuthService {

    private final UserServiceClient userServiceClient;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Value("${internal.service.secret:VendoraLocalInternalSecret2026}")
    private String internalServiceSecret = "VendoraLocalInternalSecret2026";

    public AuthService(
            UserServiceClient userServiceClient,
            JwtService jwtService,
            PasswordEncoder passwordEncoder) {

        this.userServiceClient = userServiceClient;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    // Authenticate user and generate JWT
    public LoginResponse login(LoginRequest request) {

        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        AuthUserDetails user;

        // 1. Fetch user details from User Service via OpenFeign
        try {
            user = userServiceClient.getUserByEmail(email, internalServiceSecret);
        } catch (FeignException.NotFound ex) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Invalid email or password");
        } catch (FeignException ex) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "Unable to communicate with User Service: " + ex.getMessage());
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "Unable to reach User Service");
        }

        // 2. Verify password using BCrypt
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        // 3. Check account status
        if (!"ACTIVE".equalsIgnoreCase(user.getAccountStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Your account is not active");
        }

        // 4. Generate JWT
        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );

        // 5. Return login response
        return new LoginResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getRole(),
                jwtService.getExpirationInMillis()
        );
    }

    // Validate token and return claims
    public TokenValidationResponse validateToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return TokenValidationResponse.invalid("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7).trim();

        try {
            Claims claims = jwtService.extractAllClaims(token);
            Long userId = Long.parseLong(claims.getSubject());
            String email = claims.get("email", String.class);
            String role = claims.get("role", String.class);

            return TokenValidationResponse.valid(userId, email, role);
        } catch (JwtException | IllegalArgumentException ex) {
            return TokenValidationResponse.invalid("Invalid or expired token: " + ex.getMessage());
        }
    }
}
