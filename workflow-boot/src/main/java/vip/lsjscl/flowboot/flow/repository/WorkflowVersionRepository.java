package vip.lsjscl.flowboot.flow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vip.lsjscl.flowboot.flow.entity.WorkflowVersion;
import java.util.List;

public interface WorkflowVersionRepository extends JpaRepository<WorkflowVersion, Long> {
    @Query("SELECT COALESCE(MAX(v.version), 0) FROM WorkflowVersion v WHERE v.workflowId = ?1")
    Integer findMaxVersionByWorkflowId(Long workflowId);
    
    WorkflowVersion findByWorkflowIdAndVersion(Long workflowId, Integer version);

    // 添加这个方法，用于获取指定工作流的所有版本
    List<WorkflowVersion> findByWorkflowIdOrderByVersionDesc(Long workflowId);
} 