package com.vendora.user.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.vendora.user.dto.RepresentativeApplicationRequest;
import com.vendora.user.dto.RepresentativeResponse;
import com.vendora.user.entity.AppRepresentative;
import com.vendora.user.entity.Role;
import com.vendora.user.entity.User;
import com.vendora.user.repository.AppRepresentativeRepository;
import com.vendora.user.repository.UserRepository;

@Service
public class RepresentativeService {

    private final AppRepresentativeRepository representativeRepository;
    private final UserRepository userRepository;

    public RepresentativeService(
            AppRepresentativeRepository representativeRepository,
            UserRepository userRepository) {
        this.representativeRepository = representativeRepository;
        this.userRepository = userRepository;
    }

    // 1. Submit application for App Representative
    @Transactional
    public RepresentativeResponse apply(Long userId, RepresentativeApplicationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (representativeRepository.existsByUserId(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "An application for this user already exists");
        }

        AppRepresentative rep = new AppRepresentative();
        rep.setUserId(userId);
        rep.setApprovalStatus("PENDING");
        rep.setReason(request != null ? request.getReason() : null);

        AppRepresentative saved = representativeRepository.save(rep);
        return mapToResponse(saved);
    }

    // 2. Admin review (approve or reject)
    @Transactional
    public RepresentativeResponse review(Long representativeId, Long adminId, String newStatus) {
        String normalizedStatus = newStatus != null ? newStatus.trim().toUpperCase() : "";
        if ("ACCEPTED".equals(normalizedStatus)) {
            normalizedStatus = "APPROVED";
        }

        if (!"APPROVED".equals(normalizedStatus) && !"REJECTED".equals(normalizedStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Status must be either APPROVED (or ACCEPTED) or REJECTED");
        }

        // Accept either the Application ID or the applicant's User ID for convenience
        String finalStatus = normalizedStatus;
        AppRepresentative rep = representativeRepository.findById(representativeId)
                .or(() -> representativeRepository.findByUserId(representativeId))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Representative application not found with ID or User ID: " + representativeId));

        rep.setApprovalStatus(finalStatus);
        rep.setApprovedBy(adminId);
        rep.setApprovedAt(LocalDateTime.now());

        // When approved, promote user to APP_REPRESENTATIVE role
        if ("APPROVED".equalsIgnoreCase(finalStatus)) {
            userRepository.findById(rep.getUserId()).ifPresent(user -> {
                user.setRole(Role.APP_REPRESENTATIVE);
                userRepository.save(user);
            });
        }

        return mapToResponse(representativeRepository.save(rep));
    }

    // 3. Get status by user ID
    @Transactional(readOnly = true)
    public RepresentativeResponse getByUserId(Long userId) {
        AppRepresentative rep = representativeRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No representative application found for user ID: " + userId));

        return mapToResponse(rep);
    }

    // 4. Get all applications
    @Transactional(readOnly = true)
    public List<RepresentativeResponse> getAll() {
        return representativeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private RepresentativeResponse mapToResponse(AppRepresentative rep) {
        return new RepresentativeResponse(
                rep.getId(),
                rep.getUserId(),
                rep.getApprovalStatus(),
                rep.getApprovedBy(),
                rep.getApprovedAt(),
                rep.getReason(),
                rep.getCreatedAt()
        );
    }
}
