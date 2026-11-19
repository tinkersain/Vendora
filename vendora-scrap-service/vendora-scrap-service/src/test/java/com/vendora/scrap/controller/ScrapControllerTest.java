package com.vendora.scrap.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

import com.vendora.scrap.dto.ScrapAssignmentDto;
import com.vendora.scrap.dto.ScrapRequestDto;
import com.vendora.scrap.dto.ScrapResponseDto;
import com.vendora.scrap.entity.QuantityUnit;
import com.vendora.scrap.entity.ScrapMaterialType;
import com.vendora.scrap.entity.ScrapStatus;
import com.vendora.scrap.service.ScrapService;

@ExtendWith(MockitoExtension.class)
class ScrapControllerTest {

    @Mock
    private ScrapService scrapService;

    @InjectMocks
    private ScrapController scrapController;

    private ScrapResponseDto sampleResponse;

    @BeforeEach
    void setUp() {
        sampleResponse = new ScrapResponseDto();
        sampleResponse.setId(1L);
        sampleResponse.setUserId(10L);
        sampleResponse.setMaterialType(ScrapMaterialType.CARDBOARD);
        sampleResponse.setEstimatedQuantity(BigDecimal.valueOf(15.5));
        sampleResponse.setQuantityUnit(QuantityUnit.KG);
        sampleResponse.setStatus(ScrapStatus.PENDING);
        sampleResponse.setPickupLocation("Hostel B, Room 204");
        sampleResponse.setPreferredPickupTime(LocalDateTime.now().plusDays(1));
        sampleResponse.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("createRequest - returns 201 Created")
    void testCreateRequest_Success() {
        ScrapRequestDto dto = new ScrapRequestDto();
        dto.setMaterialType(ScrapMaterialType.CARDBOARD);
        when(scrapService.createRequest(any(ScrapRequestDto.class))).thenReturn(sampleResponse);

        ResponseEntity<ScrapResponseDto> response = scrapController.createRequest(10L, dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    @DisplayName("createRequest - throws 401 Unauthorized if user is null")
    void testCreateRequest_Unauthorized() {
        ScrapRequestDto dto = new ScrapRequestDto();
        assertThrows(ResponseStatusException.class,
                () -> scrapController.createRequest(null, dto));
    }

    @Test
    @DisplayName("getMyRequests - returns 200 OK")
    void testGetMyRequests_Success() {
        when(scrapService.getRequestsByUser(10L)).thenReturn(Collections.singletonList(sampleResponse));

        ResponseEntity<List<ScrapResponseDto>> response = scrapController.getMyRequests(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("getMyRequests - throws 401 Unauthorized when user is null")
    void testGetMyRequests_Unauthorized() {
        assertThrows(ResponseStatusException.class,
                () -> scrapController.getMyRequests(null));
    }

    @Test
    @DisplayName("getAllRequests - Admin permitted")
    void testGetAllRequests_Admin() {
        when(scrapService.getAllRequests()).thenReturn(Collections.singletonList(sampleResponse));

        ResponseEntity<List<ScrapResponseDto>> response = scrapController.getAllRequests("ADMIN");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("getAllRequests - Student forbidden")
    void testGetAllRequests_Forbidden() {
        assertThrows(ResponseStatusException.class,
                () -> scrapController.getAllRequests("STUDENT"));
    }

    @Test
    @DisplayName("getRequestById - Owner permitted")
    void testGetRequestById_Owner() {
        when(scrapService.getRequestById(1L)).thenReturn(sampleResponse);

        ResponseEntity<ScrapResponseDto> response = scrapController.getRequestById(1L, 10L, "STUDENT");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    @DisplayName("getRequestById - Other user forbidden")
    void testGetRequestById_Forbidden() {
        when(scrapService.getRequestById(1L)).thenReturn(sampleResponse);

        assertThrows(ResponseStatusException.class,
                () -> scrapController.getRequestById(1L, 99L, "STUDENT"));
    }

    @Test
    @DisplayName("getRequestsByUser - Admin permitted")
    void testGetRequestsByUser_Admin() {
        when(scrapService.getRequestsByUser(10L)).thenReturn(Collections.singletonList(sampleResponse));

        ResponseEntity<List<ScrapResponseDto>> response = scrapController.getRequestsByUser(10L, "ADMIN");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("getRequestsByUser - Student forbidden")
    void testGetRequestsByUser_Forbidden() {
        assertThrows(ResponseStatusException.class,
                () -> scrapController.getRequestsByUser(10L, "STUDENT"));
    }

    @Test
    @DisplayName("getRequestsByStaff - Collection staff permitted")
    void testGetRequestsByStaff_Staff() {
        when(scrapService.getRequestsByStaff(50L)).thenReturn(Collections.singletonList(sampleResponse));

        ResponseEntity<List<ScrapResponseDto>> response = scrapController.getRequestsByStaff(50L, "COLLECTION_STAFF");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("getRequestsByStaff - Student forbidden")
    void testGetRequestsByStaff_Forbidden() {
        assertThrows(ResponseStatusException.class,
                () -> scrapController.getRequestsByStaff(50L, "STUDENT"));
    }

    @Test
    @DisplayName("assignStaff - Admin permitted")
    void testAssignStaff_Admin() {
        ScrapAssignmentDto assignDto = new ScrapAssignmentDto();
        assignDto.setAssignedStaffId(50L);
        when(scrapService.assignStaff(eq(1L), any(ScrapAssignmentDto.class))).thenReturn(sampleResponse);

        ResponseEntity<ScrapResponseDto> response = scrapController.assignStaff(1L, "ADMIN", assignDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("assignStaff - Non-admin forbidden")
    void testAssignStaff_Forbidden() {
        ScrapAssignmentDto assignDto = new ScrapAssignmentDto();
        assertThrows(ResponseStatusException.class,
                () -> scrapController.assignStaff(1L, "STUDENT", assignDto));
    }

    @Test
    @DisplayName("updateStatus - Collection staff permitted")
    void testUpdateStatus_Staff() {
        sampleResponse.setStatus(ScrapStatus.COLLECTED);
        when(scrapService.updateStatus(1L, ScrapStatus.COLLECTED)).thenReturn(sampleResponse);

        ResponseEntity<ScrapResponseDto> response = scrapController.updateStatus(1L, 50L, "COLLECTION_STAFF", ScrapStatus.COLLECTED);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ScrapStatus.COLLECTED, response.getBody().getStatus());
    }

    @Test
    @DisplayName("updateStatus - Student forbidden")
    void testUpdateStatus_Forbidden() {
        assertThrows(ResponseStatusException.class,
                () -> scrapController.updateStatus(1L, 10L, "STUDENT", ScrapStatus.COLLECTED));
    }

    @Test
    @DisplayName("cancelRequest - returns 200 OK")
    void testCancelRequest() {
        sampleResponse.setStatus(ScrapStatus.CANCELLED);
        when(scrapService.cancelRequest(1L, 10L)).thenReturn(sampleResponse);

        ResponseEntity<ScrapResponseDto> response = scrapController.cancelRequest(1L, 10L, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ScrapStatus.CANCELLED, response.getBody().getStatus());
    }
}
