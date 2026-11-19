package com.vendora.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.vendora.user.dto.AuthUserDetailsResponse;
import com.vendora.user.dto.UserProfileUpdateRequest;
import com.vendora.user.dto.UserRegistrationRequest;
import com.vendora.user.dto.UserResponse;
import com.vendora.user.entity.AccountStatus;
import com.vendora.user.entity.Role;
import com.vendora.user.entity.User;
import com.vendora.user.repository.UserRepository;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void testRegisterUserSuccess() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setFullName("John Doe");
        request.setEmail("john@example.com");
        request.setPhone("9876543210");
        request.setRegistrationNumber("REG123");
        request.setPassword("Secret123");

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.existsByRegistrationNumber("REG123")).thenReturn(false);
        when(passwordEncoder.encode("Secret123")).thenReturn("encodedSecret");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setFullName("John Doe");
        savedUser.setEmail("john@example.com");
        savedUser.setPhone("9876543210");
        savedUser.setRegistrationNumber("REG123");
        savedUser.setPasswordHash("encodedSecret");
        savedUser.setRole(Role.STUDENT);
        savedUser.setAccountStatus(AccountStatus.ACTIVE);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.registerUser(request);

        assertNotNull(response);
        assertEquals("John Doe", response.getFullName());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("9876543210", response.getPhone());
        assertEquals(Role.STUDENT, response.getRole());
        assertEquals(AccountStatus.ACTIVE, response.getAccountStatus());
    }

    @Test
    void testRegisterUserDuplicateEmail() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setFullName("John Doe");
        request.setEmail("john@example.com");
        request.setPassword("Secret123");

        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.registerUser(request));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void testRegisterUserDuplicateRegistrationNumber() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setFullName("John Doe");
        request.setEmail("john@example.com");
        request.setRegistrationNumber("REG123");
        request.setPassword("Secret123");

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.existsByRegistrationNumber("REG123")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.registerUser(request));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void testGetUserByIdSuccess() {
        User user = new User();
        user.setId(5L);
        user.setFullName("Alice");
        user.setEmail("alice@vendora.com");
        user.setRole(Role.STUDENT);
        user.setAccountStatus(AccountStatus.ACTIVE);

        when(userRepository.findById(5L)).thenReturn(Optional.of(user));

        UserResponse response = userService.getUserById(5L);
        assertEquals(5L, response.getId());
        assertEquals("Alice", response.getFullName());
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.getUserById(99L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testUpdateUserProfile() {
        User user = new User();
        user.setId(2L);
        user.setFullName("Bob");
        user.setEmail("bob@vendora.com");
        user.setRole(Role.STUDENT);
        user.setAccountStatus(AccountStatus.ACTIVE);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserProfileUpdateRequest updateReq = new UserProfileUpdateRequest("Bob Updated", "1234567890");
        UserResponse updated = userService.updateUserProfile(2L, updateReq);

        assertEquals("Bob Updated", updated.getFullName());
        assertEquals("1234567890", updated.getPhone());
    }

    @Test
    void testUpdateAccountStatus() {
        User user = new User();
        user.setId(3L);
        user.setAccountStatus(AccountStatus.ACTIVE);

        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse resp = userService.updateAccountStatus(3L, AccountStatus.SUSPENDED);
        assertEquals(AccountStatus.SUSPENDED, resp.getAccountStatus());
    }

    @Test
    void testGetAuthUserDetailsSuccess() {
        User user = new User();
        user.setId(10L);
        user.setEmail("auth@vendora.com");
        user.setPasswordHash("hash123");
        user.setRole(Role.FACULTY);
        user.setAccountStatus(AccountStatus.ACTIVE);

        when(userRepository.findByEmail("auth@vendora.com")).thenReturn(Optional.of(user));

        AuthUserDetailsResponse resp = userService.getAuthUserDetails("auth@vendora.com");
        assertEquals(10L, resp.getId());
        assertEquals("FACULTY", resp.getRole());
        assertEquals("ACTIVE", resp.getAccountStatus());
    }

    @Test
    void testGetAuthUserDetailsNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.getAuthUserDetails("none@vendora.com"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
