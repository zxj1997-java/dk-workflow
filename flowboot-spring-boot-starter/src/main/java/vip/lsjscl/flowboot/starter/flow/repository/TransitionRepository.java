package vip.lsjscl.flowboot.starter.flow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vip.lsjscl.flowboot.starter.flow.entity.Activity;
import vip.lsjscl.flowboot.starter.flow.entity.Transition;

import java.util.List;

/**
 * 变迁存储库
 *
 * @author zhangxingju
 * @date 2025/02/14
 */
@Repository
public interface TransitionRepository extends JpaRepository<Transition, String> {
    // 根据起始活动查询变迁记录
    List<Transition> findByFromActivity(Activity activity);

    // 根据目标活动查询变迁记录，用于获取上一个活动节点
    List<Transition> findByToActivity(Activity activity);

    // 根据流程版本id查询变迁记录
    List<Transition> findByWorkflowVersionId(String workflowVersionId);

    // 其它方法
} 