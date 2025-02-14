package vip.lsjscl.flowboot.flow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vip.lsjscl.flowboot.flow.entity.RuntimeTask;

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

    // 查询所有不同的业务ID
    @Query("SELECT DISTINCT r.businessId FROM RuntimeTask r")
    List<String> findDistinctBusinessIds();
    
    // 根据业务ID查询所有任务
    List<RuntimeTask> findByBusinessId(String businessId);

    List<RuntimeTask> findByStatus(TaskStatus status);
} 