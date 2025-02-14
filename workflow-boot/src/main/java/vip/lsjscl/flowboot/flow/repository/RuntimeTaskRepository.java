package vip.lsjscl.flowboot.flow.repository;

import vip.lsjscl.flowboot.flow.entity.RuntimeTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import vip.lsjscl.flowboot.flow.dict.TaskStatus;

/**
 * 运行时任务存储库
 *
 * @author zhangxingju
 * @date 2025/02/14
 */
public interface RuntimeTaskRepository extends JpaRepository<RuntimeTask, Long> {
    // 根据业务ID和任务状态查找唯一待办理任务记录（假设每个业务只有一条待办理任务）
    Optional<RuntimeTask> findByBusinessIdAndStatus(String businessId, TaskStatus status);

    // 新增根据业务ID查询运行时任务的方法
    List<RuntimeTask> findByBusinessId(String businessId);
} 