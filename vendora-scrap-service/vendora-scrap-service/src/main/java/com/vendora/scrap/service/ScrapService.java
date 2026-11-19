 
package com.vendora.scrap.service;
 
import java.util.List;
import java.util.stream.Collectors;
 
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
 
import com.vendora.scrap.dto.ScrapAssignmentDto;
import com.vendora.scrap.dto.ScrapRequestDto;
import com.vendora.scrap.dto.ScrapResponseDto;
import com.vendora.scrap.entity.ScrapRequest;
import com.vendora.scrap.entity.ScrapStatus;
import com.vendora.scrap.repository.ScrapRepository;
 
@Service
@Transactional
public class ScrapService {
 
    private final ScrapRepository scrapRepository;
 
    public ScrapService(ScrapRepository scrapRepository) {
        this.scrapRepository = scrapRepository;
    }
 
    // Create a new scrap collection request
    public ScrapResponseDto createRequest(ScrapRequestDto dto) {
 
        ScrapRequest request = new ScrapRequest();
 
        request.setUserId(dto.getUserId());
        request.setMaterialType(dto.getMaterialType());
        request.setDescription(dto.getDescription());
        request.setEstimatedQuantity(dto.getEstimatedQuantity());
        request.setQuantityUnit(dto.getQuantityUnit());
        request.setPickupLocation(dto.getPickupLocation());
        request.setPreferredPickupTime(dto.getPreferredPickupTime());
        request.setStatus(ScrapStatus.PENDING);
 
        return mapToResponse(scrapRepository.save(request));
    }
 
    // Get all scrap requests (representative/admin view)
    @Transactional(readOnly = true)
    public List<ScrapResponseDto> getAllRequests() {
 
        return scrapRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
 
    // Get a request by ID
    @Transactional(readOnly = true)
    public ScrapResponseDto getRequestById(Long id) {
 
        ScrapRequest request = findRequestOrThrow(id);
 
        return mapToResponse(request);
    }
 
    // Get requests submitted by a particular user
    @Transactional(readOnly = true)
    public List<ScrapResponseDto> getRequestsByUser(Long userId) {
 
        return scrapRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
 
    // Get requests assigned to a particular staff member
    @Transactional(readOnly = true)
    public List<ScrapResponseDto> getRequestsByStaff(Long staffId) {
 
        return scrapRepository.findByAssignedStaffId(staffId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
 
    // Assign collection staff to a request
    public ScrapResponseDto assignStaff(
            Long requestId, ScrapAssignmentDto dto) {
 
        ScrapRequest request = findRequestOrThrow(requestId);
 
        if (request.getStatus() != ScrapStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only pending requests can be assigned");
        }
 
        request.setAssignedStaffId(dto.getAssignedStaffId());
        request.setStatus(ScrapStatus.ASSIGNED);
 
        return mapToResponse(scrapRepository.save(request));
    }
 
    // Update collection status
    public ScrapResponseDto updateStatus(
            Long requestId, ScrapStatus newStatus) {
 
        ScrapRequest request = findRequestOrThrow(requestId);
 
        ScrapStatus currentStatus = request.getStatus();
 
        boolean validTransition =
                (currentStatus == ScrapStatus.ASSIGNED
                        && newStatus == ScrapStatus.PICKUP_IN_PROGRESS)
                || (currentStatus == ScrapStatus.PICKUP_IN_PROGRESS
                        && newStatus == ScrapStatus.COLLECTED)
                || (currentStatus == ScrapStatus.COLLECTED
                        && newStatus == ScrapStatus.CLOSED);
 
        if (!validTransition) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid status transition from "
                            + currentStatus + " to " + newStatus);
        }
 
        request.setStatus(newStatus);
 
        return mapToResponse(scrapRepository.save(request));
    }
 
    // Cancel a request
    public ScrapResponseDto cancelRequest(Long requestId, Long userId) {
 
        ScrapRequest request = findRequestOrThrow(requestId);
 
        if (!request.getUserId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot cancel another user's request");
        }
 
        if (request.getStatus() != ScrapStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only pending requests can be cancelled");
        }
 
        request.setStatus(ScrapStatus.CANCELLED);
 
        return mapToResponse(scrapRepository.save(request));
    }
 
    private ScrapRequest findRequestOrThrow(Long id) {
 
        return scrapRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Scrap request not found with ID: " + id));
    }
 
    // Convert entity into response DTO
    private ScrapResponseDto mapToResponse(ScrapRequest request) {
 
        ScrapResponseDto dto = new ScrapResponseDto();
 
        dto.setId(request.getId());
        dto.setUserId(request.getUserId());
        dto.setMaterialType(request.getMaterialType());
        dto.setDescription(request.getDescription());
        dto.setEstimatedQuantity(request.getEstimatedQuantity());
        dto.setQuantityUnit(request.getQuantityUnit());
        dto.setPickupLocation(request.getPickupLocation());
        dto.setPreferredPickupTime(request.getPreferredPickupTime());
        dto.setAssignedStaffId(request.getAssignedStaffId());
        dto.setStatus(request.getStatus());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setUpdatedAt(request.getUpdatedAt());
 
        return dto;
    }
}
 