package vip.lsjscl.flowboot.flow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.lsjscl.flowboot.flow.entity.WorkflowInstance;
import java.util.List;

public interface WorkflowInstanceRepository extends JpaRepository<WorkflowInstance, Long> {
    List<WorkflowInstance> findByWorkflowIdOrderByCreateTimeDesc(Long workflowId);
} 