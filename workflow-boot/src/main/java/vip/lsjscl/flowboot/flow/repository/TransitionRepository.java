package vip.lsjscl.flowboot.flow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.lsjscl.flowboot.flow.entity.Activity;
import vip.lsjscl.flowboot.flow.entity.Transition;
import java.util.List;

public interface TransitionRepository extends JpaRepository<Transition, Long> {
    // 根据起始活动查询变迁记录
    List<Transition> findByFromActivity(Activity fromActivity);

    // 根据流程版本id查询变迁记录
    List<Transition> findByWorkflowVersionId(String workflowVersionId);

    // 其它方法
} 