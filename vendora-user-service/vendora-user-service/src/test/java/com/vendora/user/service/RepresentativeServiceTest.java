package com.vendora.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.vendora.user.dto.RepresentativeApplicationRequest;
import com.vendora.user.dto.RepresentativeResponse;
import com.vendora.user.entity.AppRepresentative;
import com.vendora.user.entity.Role;
import com.vendora.user.entity.User;
import com.vendora.user.repository.AppRepresentativeRepository;
import com.vendora.user.repository.UserRepository;

class RepresentativeServiceTest {

    private AppRepresentativeRepository representativeRepository;
    private UserRepository userRepository;
    private RepresentativeService representativeService;

    @BeforeEach
    void setUp() {
        representativeRepository = mock(AppRepresentativeRepository.class);
        userRepository = mock(UserRepository.class);
        representativeService = new RepresentativeService(representativeRepository, userRepository);
    }

    @Test
    void testApplySuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(representativeRepository.existsByUserId(1L)).thenReturn(false);

        AppRepresentative saved = new AppRepresentative();
        saved.setId(10L);
        saved.setUserId(1L);
        saved.setApprovalStatus("PENDING");
        saved.setReason("Interested in campus tech");

        when(representativeRepository.save(any(AppRepresentative.class))).thenReturn(saved);

        RepresentativeResponse resp = representativeService.apply(1L, new RepresentativeApplicationRequest("Interested in campus tech"));
        assertNotNull(resp);
        assertEquals(10L, resp.getId());
        assertEquals("PENDING", resp.getApprovalStatus());
    }

    @Test
    void testApplyUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> representativeService.apply(99L, new RepresentativeApplicationRequest("Reason")));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testApplyAlreadyExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(representativeRepository.existsByUserId(1L)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> representativeService.apply(1L, new RepresentativeApplicationRequest("Reason")));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void testReviewApprove() {
        AppRepresentative rep = new AppRepresentative();
        rep.setId(10L);
        rep.setUserId(2L);
        rep.setApprovalStatus("PENDING");

        User user = new User();
        user.setId(2L);
        user.setRole(Role.STUDENT);

        when(representativeRepository.findById(10L)).thenReturn(Optional.of(rep));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(representativeRepository.save(any(AppRepresentative.class))).thenAnswer(i -> i.getArgument(0));

        RepresentativeResponse resp = representativeService.review(10L, 999L, "APPROVED");

        assertEquals("APPROVED", resp.getApprovalStatus());
        assertEquals(999L, resp.getApprovedBy());
        assertEquals(Role.APP_REPRESENTATIVE, user.getRole());
    }

    @Test
    void testReviewReject() {
        AppRepresentative rep = new AppRepresentative();
        rep.setId(10L);
        rep.setUserId(2L);
        rep.setApprovalStatus("PENDING");

        when(representativeRepository.findById(10L)).thenReturn(Optional.of(rep));
        when(representativeRepository.save(any(AppRepresentative.class))).thenAnswer(i -> i.getArgument(0));

        RepresentativeResponse resp = representativeService.review(10L, 999L, "REJECTED");
        assertEquals("REJECTED", resp.getApprovalStatus());
    }

    @Test
    void testReviewInvalidStatus() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> representativeService.review(10L, 999L, "INVALID_STATUS"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testGetByUserIdSuccess() {
        AppRepresentative rep = new AppRepresentative();
        rep.setId(10L);
        rep.setUserId(1L);
        rep.setApprovalStatus("APPROVED");

        when(representativeRepository.findByUserId(1L)).thenReturn(Optional.of(rep));

        RepresentativeResponse resp = representativeService.getByUserId(1L);
        assertEquals(10L, resp.getId());
    }

    @Test
    void testGetAll() {
        AppRepresentative rep = new AppRepresentative();
        rep.setId(1L);
        when(representativeRepository.findAll()).thenReturn(List.of(rep));

        List<RepresentativeResponse> list = representativeService.getAll();
        assertEquals(1, list.size());
    }
}
