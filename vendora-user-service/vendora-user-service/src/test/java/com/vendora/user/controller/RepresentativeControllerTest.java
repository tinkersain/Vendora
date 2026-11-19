package com.vendora.user.controller;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import com.vendora.user.dto.RepresentativeApplicationRequest;
import com.vendora.user.dto.RepresentativeResponse;
import com.vendora.user.service.RepresentativeService;

@ExtendWith(MockitoExtension.class)
class RepresentativeControllerTest {

    @Mock
    private RepresentativeService representativeService;

    @InjectMocks
    private RepresentativeController representativeController;

    private RepresentativeResponse sampleResponse;

    @BeforeEach
    void setUp() {
        sampleResponse = new RepresentativeResponse();
        sampleResponse.setId(1L);
        sampleResponse.setUserId(10L);
        sampleResponse.setApprovalStatus("PENDING");
        sampleResponse.setReason("Interested in campus sustainability");
    }

    @Test
    @DisplayName("apply - should return 201 Created")
    void apply_Success() {
        RepresentativeApplicationRequest req = new RepresentativeApplicationRequest("Interested in campus sustainability");

        when(representativeService.apply(eq(10L), any())).thenReturn(sampleResponse);

        ResponseEntity<RepresentativeResponse> resp = representativeController.apply(10L, req);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals(1L, resp.getBody().getId());
    }

    @Test
    @DisplayName("getMyStatus - should return status")
    void getMyStatus_Success() {
        when(representativeService.getByUserId(10L)).thenReturn(sampleResponse);

        ResponseEntity<RepresentativeResponse> resp = representativeController.getMyStatus(10L);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("reviewApplication - Admin can review")
    void reviewApplication_Admin() {
        when(representativeService.review(1L, 99L, "APPROVED")).thenReturn(sampleResponse);

        ResponseEntity<RepresentativeResponse> resp = representativeController.reviewApplication(1L, "APPROVED", 99L, "ADMIN");

        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("reviewApplication - Non-admin throws 403 Forbidden")
    void reviewApplication_Forbidden() {
        assertThrows(ResponseStatusException.class,
                () -> representativeController.reviewApplication(1L, "APPROVED", 99L, "STUDENT"));
    }

    @Test
    @DisplayName("getAllApplications - Admin can list applications")
    void getAllApplications_Admin() {
        when(representativeService.getAll()).thenReturn(Collections.singletonList(sampleResponse));

        ResponseEntity<List<RepresentativeResponse>> resp = representativeController.getAllApplications("ADMIN");

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(1, resp.getBody().size());
    }

    @Test
    @DisplayName("getAllApplications - Non-admin throws 403 Forbidden")
    void getAllApplications_Forbidden() {
        assertThrows(ResponseStatusException.class,
                () -> representativeController.getAllApplications("STUDENT"));
    }
}
