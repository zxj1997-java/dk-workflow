package vip.lsjscl.flowboot.leave.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.lsjscl.flowboot.leave.entity.LeaveInfo;

import java.util.List;

/**
 * 请假 仓库
 *
 * @author zhangxingju
 * @date 2025/02/15
 */
public interface LeaveInfoRepository extends JpaRepository<LeaveInfo, Long> {

    List<LeaveInfo> findByStatus(String status);

    List<LeaveInfo> findByStatusIn(List<String> statuses);
} 