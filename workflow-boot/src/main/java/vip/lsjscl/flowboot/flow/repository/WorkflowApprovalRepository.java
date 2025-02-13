package vip.lsjscl.flowboot.flow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.lsjscl.flowboot.flow.entity.WorkflowApproval;
import java.util.List;

public interface WorkflowApprovalRepository extends JpaRepository<WorkflowApproval, Long> {
    List<WorkflowApproval> findByInstanceIdOrderByCreateTimeDesc(Long instanceId);
} 