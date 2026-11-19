package com.vendora.user.dto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.vendora.user.entity.AccountStatus;
import com.vendora.user.entity.AppRepresentative;
import com.vendora.user.entity.Role;
import com.vendora.user.entity.User;

class UserDtoAndEntityTest {

    @Test
    @DisplayName("User Entity - test getters, setters, onCreate")
    void testUserEntity() {
        User user = new User();
        user.setId(1L);
        user.setFullName("Aarav");
        user.setEmail("aarav@campus.edu");
        user.setPhone("9999999999");
        user.setRegistrationNumber("REG-1");
        user.setPasswordHash("hash123");
        user.setRole(Role.STUDENT);
        user.setAccountStatus(AccountStatus.ACTIVE);

        assertEquals(1L, user.getId());
        assertEquals("Aarav", user.getFullName());
        assertEquals("aarav@campus.edu", user.getEmail());
        assertEquals("9999999999", user.getPhone());
        assertEquals("REG-1", user.getRegistrationNumber());
        assertEquals("hash123", user.getPasswordHash());
        assertEquals(Role.STUDENT, user.getRole());
        assertEquals(AccountStatus.ACTIVE, user.getAccountStatus());
    }

    @Test
    @DisplayName("AppRepresentative Entity - test getters and setters")
    void testAppRepresentativeEntity() {
        AppRepresentative rep = new AppRepresentative();
        rep.setId(5L);
        rep.setUserId(10L);
        rep.setApprovalStatus("APPROVED");
        rep.setApprovedBy(1L);
        rep.setApprovedAt(LocalDateTime.now());
        rep.setReason("Good student");
        rep.setCreatedAt(LocalDateTime.now());

        assertEquals(5L, rep.getId());
        assertEquals(10L, rep.getUserId());
        assertEquals("APPROVED", rep.getApprovalStatus());
        assertEquals(1L, rep.getApprovedBy());
        assertNotNull(rep.getApprovedAt());
        assertEquals("Good student", rep.getReason());
        assertNotNull(rep.getCreatedAt());
    }

    @Test
    @DisplayName("DTOs - test all getters and setters")
    void testDtos() {
        UserRegistrationRequest reg = new UserRegistrationRequest();
        reg.setFullName("Name");
        reg.setEmail("email@campus.edu");
        reg.setPhone("1234567890");
        reg.setRegistrationNumber("REG");
        reg.setPassword("Pass@123");

        assertEquals("Name", reg.getFullName());
        assertEquals("email@campus.edu", reg.getEmail());
        assertEquals("1234567890", reg.getPhone());
        assertEquals("REG", reg.getRegistrationNumber());
        assertEquals("Pass@123", reg.getPassword());

        UserProfileUpdateRequest update = new UserProfileUpdateRequest();
        update.setFullName("New Name");
        update.setPhone("0987654321");

        assertEquals("New Name", update.getFullName());
        assertEquals("0987654321", update.getPhone());

        RepresentativeApplicationRequest repReq = new RepresentativeApplicationRequest();
        repReq.setReason("Good reason");
        assertEquals("Good reason", repReq.getReason());

        RepresentativeResponse repResp = new RepresentativeResponse(1L, 2L, "PENDING", null, null, "Reason", LocalDateTime.now());
        assertEquals(1L, repResp.getId());
        assertEquals(2L, repResp.getUserId());
        assertEquals("PENDING", repResp.getApprovalStatus());

        UserResponse userResp = new UserResponse(1L, "User", "user@campus.edu", "9999999999", "REG-1", Role.STUDENT, AccountStatus.ACTIVE, LocalDateTime.now());
        assertEquals(1L, userResp.getId());
        assertEquals("User", userResp.getFullName());
        assertEquals("user@campus.edu", userResp.getEmail());

        AuthUserDetailsResponse authResp = new AuthUserDetailsResponse(2L, "auth@campus.edu", "h", "ADMIN", "ACTIVE");
        assertEquals(2L, authResp.getId());
        assertEquals("auth@campus.edu", authResp.getEmail());
        assertEquals("h", authResp.getPasswordHash());
        assertEquals("ADMIN", authResp.getRole());
        assertEquals("ACTIVE", authResp.getAccountStatus());
    }

    @Test
    @DisplayName("Enums - test Role and AccountStatus values")
    void testEnums() {
        assertEquals(Role.STUDENT, Role.valueOf("STUDENT"));
        assertEquals(Role.APP_REPRESENTATIVE, Role.valueOf("APP_REPRESENTATIVE"));
        assertEquals(Role.ADMIN, Role.valueOf("ADMIN"));
        assertEquals(Role.MAINTENANCE_STAFF, Role.valueOf("MAINTENANCE_STAFF"));

        assertEquals(AccountStatus.ACTIVE, AccountStatus.valueOf("ACTIVE"));
        assertEquals(AccountStatus.INACTIVE, AccountStatus.valueOf("INACTIVE"));
        assertEquals(AccountStatus.SUSPENDED, AccountStatus.valueOf("SUSPENDED"));
    }
}
