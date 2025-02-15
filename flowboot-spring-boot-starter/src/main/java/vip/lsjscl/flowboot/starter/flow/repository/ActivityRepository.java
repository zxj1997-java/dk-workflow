package vip.lsjscl.flowboot.starter.flow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vip.lsjscl.flowboot.starter.flow.entity.Activity;

import java.util.List;

/**
 * 活动存储库
 *
 * @author zhangxingju
 * @date 2025/02/14
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    // 根据工作流版本ID查询所有活动
    List<Activity> findByWorkflowVersionId(Long workflowVersionId);
} 