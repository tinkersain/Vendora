package com.vendora.user.service;

import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.vendora.user.dto.AuthUserDetailsResponse;
import com.vendora.user.dto.UserProfileUpdateRequest;
import com.vendora.user.dto.UserRegistrationRequest;
import com.vendora.user.dto.UserResponse;
import com.vendora.user.entity.AccountStatus;
import com.vendora.user.entity.Role;
import com.vendora.user.entity.User;
import com.vendora.user.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 1. Register a new user
    @Transactional
    public UserResponse registerUser(UserRegistrationRequest request) {

        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email is already registered");
        }

        String registrationNumber = null;
        if (request.getRegistrationNumber() != null && !request.getRegistrationNumber().isBlank()) {
            registrationNumber = request.getRegistrationNumber().trim().toUpperCase(Locale.ROOT);
            if (userRepository.existsByRegistrationNumber(registrationNumber)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Registration number already exists");
            }
        }

        User user = new User();

        user.setFullName(request.getFullName().trim());
        user.setEmail(email);
        user.setPhone(request.getPhone() != null ? request.getPhone().trim() : null);
        user.setRegistrationNumber(registrationNumber);

        // Never store plain-text passwords
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // Public registration defaults to student account
        user.setRole(Role.STUDENT);
        user.setAccountStatus(AccountStatus.ACTIVE);

        User savedUser = userRepository.save(user);

        return mapToUserResponse(savedUser);
    }

    // 2. Retrieve user by ID
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with ID: " + userId));

        return mapToUserResponse(user);
    }

    // 3. Update user profile
    @Transactional
    public UserResponse updateUserProfile(Long userId, UserProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with ID: " + userId));

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName().trim());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone().trim());
        }

        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }

    // 4. Admin update account status (ACTIVE, SUSPENDED, DELETED)
    @Transactional
    public UserResponse updateAccountStatus(Long userId, AccountStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with ID: " + userId));

        user.setAccountStatus(status);
        return mapToUserResponse(userRepository.save(user));
    }

    // 5. Update user role (e.g. STUDENT, FACULTY, ADMIN, APP_REPRESENTATIVE)
    @Transactional
    public UserResponse updateUserRole(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with ID: " + userId));

        user.setRole(role);
        return mapToUserResponse(userRepository.save(user));
    }

    // 6. Retrieve authentication details for Auth Service
    @Transactional(readOnly = true)
    public AuthUserDetailsResponse getAuthUserDetails(String email) {

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        return new AuthUserDetailsResponse(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole().name(),
                user.getAccountStatus().name()
        );
    }

    // Helper: Convert Entity into public Response DTO
    public UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRegistrationNumber(),
                user.getRole(),
                user.getAccountStatus(),
                user.getCreatedAt()
        );
    }
}
