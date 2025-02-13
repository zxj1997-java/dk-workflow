package vip.lsjscl.flowboot.leave.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.lsjscl.flowboot.leave.entity.LeaveInfo;

import java.util.List;

public interface LeaveInfoRepository extends JpaRepository<LeaveInfo, Long> {
    
    List<LeaveInfo> findByStatus(String status);
    
    List<LeaveInfo> findByStatusIn(List<String> statuses);
} 