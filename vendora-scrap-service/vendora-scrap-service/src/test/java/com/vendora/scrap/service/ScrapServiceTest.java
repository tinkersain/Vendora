package com.vendora.scrap.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.vendora.scrap.dto.ScrapAssignmentDto;
import com.vendora.scrap.dto.ScrapRequestDto;
import com.vendora.scrap.dto.ScrapResponseDto;
import com.vendora.scrap.entity.QuantityUnit;
import com.vendora.scrap.entity.ScrapMaterialType;
import com.vendora.scrap.entity.ScrapRequest;
import com.vendora.scrap.entity.ScrapStatus;
import com.vendora.scrap.repository.ScrapRepository;

class ScrapServiceTest {

    private ScrapRepository scrapRepository;
    private ScrapService scrapService;

    @BeforeEach
    void setUp() {
        scrapRepository = mock(ScrapRepository.class);
        scrapService = new ScrapService(scrapRepository);
    }

    @Test
    void testCreateRequestSuccess() {
        ScrapRequestDto dto = new ScrapRequestDto();
        dto.setUserId(1L);
        dto.setMaterialType(ScrapMaterialType.PAPER);
        dto.setDescription("Old notebooks");
        dto.setEstimatedQuantity(new BigDecimal("10.5"));
        dto.setQuantityUnit(QuantityUnit.KG);
        dto.setPickupLocation("Hostel 3, Room 102");

        ScrapRequest saved = new ScrapRequest();
        saved.setId(10L);
        saved.setUserId(1L);
        saved.setStatus(ScrapStatus.PENDING);
        saved.setMaterialType(ScrapMaterialType.PAPER);

        when(scrapRepository.save(any(ScrapRequest.class))).thenReturn(saved);

        ScrapResponseDto resp = scrapService.createRequest(dto);
        assertNotNull(resp);
        assertEquals(10L, resp.getId());
        assertEquals(ScrapStatus.PENDING, resp.getStatus());
    }

    @Test
    void testAssignStaffSuccess() {
        ScrapRequest request = new ScrapRequest();
        request.setId(10L);
        request.setStatus(ScrapStatus.PENDING);

        when(scrapRepository.findById(10L)).thenReturn(Optional.of(request));
        when(scrapRepository.save(any(ScrapRequest.class))).thenAnswer(i -> i.getArgument(0));

        ScrapAssignmentDto assignDto = new ScrapAssignmentDto();
        assignDto.setAssignedStaffId(55L);

        ScrapResponseDto resp = scrapService.assignStaff(10L, assignDto);
        assertEquals(ScrapStatus.ASSIGNED, resp.getStatus());
        assertEquals(55L, resp.getAssignedStaffId());
    }

    @Test
    void testUpdateStatusValidSequence() {
        ScrapRequest request = new ScrapRequest();
        request.setId(10L);
        request.setStatus(ScrapStatus.ASSIGNED);

        when(scrapRepository.findById(10L)).thenReturn(Optional.of(request));
        when(scrapRepository.save(any(ScrapRequest.class))).thenAnswer(i -> i.getArgument(0));

        ScrapResponseDto resp = scrapService.updateStatus(10L, ScrapStatus.PICKUP_IN_PROGRESS);
        assertEquals(ScrapStatus.PICKUP_IN_PROGRESS, resp.getStatus());
    }

    @Test
    void testUpdateStatusInvalidSequence() {
        ScrapRequest request = new ScrapRequest();
        request.setId(10L);
        request.setStatus(ScrapStatus.PENDING);

        when(scrapRepository.findById(10L)).thenReturn(Optional.of(request));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> scrapService.updateStatus(10L, ScrapStatus.COLLECTED));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testCancelRequestSuccess() {
        ScrapRequest request = new ScrapRequest();
        request.setId(10L);
        request.setUserId(1L);
        request.setStatus(ScrapStatus.PENDING);

        when(scrapRepository.findById(10L)).thenReturn(Optional.of(request));
        when(scrapRepository.save(any(ScrapRequest.class))).thenAnswer(i -> i.getArgument(0));

        ScrapResponseDto resp = scrapService.cancelRequest(10L, 1L);
        assertEquals(ScrapStatus.CANCELLED, resp.getStatus());
    }

    @Test
    void testCancelRequestForbidden() {
        ScrapRequest request = new ScrapRequest();
        request.setId(10L);
        request.setUserId(1L);
        request.setStatus(ScrapStatus.PENDING);

        when(scrapRepository.findById(10L)).thenReturn(Optional.of(request));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> scrapService.cancelRequest(10L, 999L));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void testGetAllRequests() {
        ScrapRequest req = new ScrapRequest();
        req.setId(1L);
        when(scrapRepository.findAll()).thenReturn(java.util.List.of(req));

        java.util.List<ScrapResponseDto> list = scrapService.getAllRequests();
        assertEquals(1, list.size());
    }

    @Test
    void testGetRequestById_Success() {
        ScrapRequest req = new ScrapRequest();
        req.setId(1L);
        when(scrapRepository.findById(1L)).thenReturn(Optional.of(req));

        ScrapResponseDto resp = scrapService.getRequestById(1L);
        assertNotNull(resp);
        assertEquals(1L, resp.getId());
    }

    @Test
    void testGetRequestById_NotFound() {
        when(scrapRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> scrapService.getRequestById(999L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testGetRequestsByUser() {
        ScrapRequest req = new ScrapRequest();
        req.setId(1L);
        req.setUserId(10L);
        when(scrapRepository.findByUserId(10L)).thenReturn(java.util.List.of(req));

        java.util.List<ScrapResponseDto> list = scrapService.getRequestsByUser(10L);
        assertEquals(1, list.size());
    }

    @Test
    void testGetRequestsByStaff() {
        ScrapRequest req = new ScrapRequest();
        req.setId(1L);
        req.setAssignedStaffId(55L);
        when(scrapRepository.findByAssignedStaffId(55L)).thenReturn(java.util.List.of(req));

        java.util.List<ScrapResponseDto> list = scrapService.getRequestsByStaff(55L);
        assertEquals(1, list.size());
    }

    @Test
    void testCancelRequest_InvalidStatus() {
        ScrapRequest request = new ScrapRequest();
        request.setId(10L);
        request.setUserId(1L);
        request.setStatus(ScrapStatus.COLLECTED);

        when(scrapRepository.findById(10L)).thenReturn(Optional.of(request));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> scrapService.cancelRequest(10L, 1L));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }
}
