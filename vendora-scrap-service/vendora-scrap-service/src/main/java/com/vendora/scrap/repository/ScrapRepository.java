 
package com.vendora.scrap.repository;
 
import java.util.List;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import com.vendora.scrap.entity.ScrapRequest;
import com.vendora.scrap.entity.ScrapStatus;
 
@Repository
public interface ScrapRepository extends JpaRepository<ScrapRequest, Long> {
 
    List<ScrapRequest> findByUserId(Long userId);
 
    List<ScrapRequest> findByAssignedStaffId(Long assignedStaffId);
 
    List<ScrapRequest> findByStatus(ScrapStatus status);
 
    List<ScrapRequest> findByUserIdAndStatus(
        Long userId, ScrapStatus status
    );
 
    List<ScrapRequest> findByAssignedStaffIdAndStatus(
        Long assignedStaffId, ScrapStatus status
    );
}
 