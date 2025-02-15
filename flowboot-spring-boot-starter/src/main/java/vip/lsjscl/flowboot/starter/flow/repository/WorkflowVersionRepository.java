package vip.lsjscl.flowboot.starter.flow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vip.lsjscl.flowboot.starter.flow.entity.WorkflowVersion;

import java.util.List;

/**
 * 工作流版本存储库
 *
 * @author zhangxingju
 * @date 2025/02/14
 */
@Repository
public interface WorkflowVersionRepository extends JpaRepository<WorkflowVersion, String> {
    @Query("SELECT COALESCE(MAX(v.version), 0) FROM WorkflowVersion v WHERE v.workflowId = ?1")
    Integer findMaxVersionByWorkflowId(String workflowId);

    WorkflowVersion findByWorkflowIdAndVersion(String workflowId, Integer version);

    // 添加这个方法，用于获取指定工作流的所有版本
    List<WorkflowVersion> findByWorkflowIdOrderByVersionDesc(String workflowId);
} 