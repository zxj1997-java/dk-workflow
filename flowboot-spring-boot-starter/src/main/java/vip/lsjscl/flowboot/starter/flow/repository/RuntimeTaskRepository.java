package vip.lsjscl.flowboot.starter.flow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vip.lsjscl.flowboot.starter.flow.dict.TaskStatus;
import vip.lsjscl.flowboot.starter.flow.entity.RuntimeTask;

import java.util.List;
import java.util.Optional;

/**
 * 运行时任务存储库
 *
 * @author zhangxingju
 * @date 2025/02/14
 */
@Repository
public interface RuntimeTaskRepository extends JpaRepository<RuntimeTask, String> {
    // 根据业务ID和任务状态查找唯一待办理任务记录（假设每个业务只有一条待办理任务）
    Optional<RuntimeTask> findByBusinessIdAndStatus(String businessId, TaskStatus status);

    // 查询所有不同的业务ID
    @Query("SELECT DISTINCT r.businessId FROM RuntimeTask r")
    List<String> findDistinctBusinessIds();
    
    // 根据业务ID查询所有任务
    List<RuntimeTask> findByBusinessId(String businessId);

    List<RuntimeTask> findByStatus(TaskStatus status);

    // 根据人员ID和任务状态查询任务
    @Query("SELECT rt FROM RuntimeTask rt WHERE rt.activity.approvers LIKE %:userId% AND rt.status = :status")
    List<RuntimeTask> findByUserIdAndStatus(String userId, TaskStatus status);

    // 根据部门ID和任务状态查询任务
    @Query("SELECT rt FROM RuntimeTask rt WHERE rt.activity.departments LIKE %:deptId% AND rt.status = :status")
    List<RuntimeTask> findByDeptIdAndStatus(String deptId, TaskStatus status);

    // 根据人员ID或部门ID查询待办任务
    @Query("SELECT rt FROM RuntimeTask rt WHERE (rt.activity.approvers LIKE %:userId% OR rt.activity.departments LIKE %:deptId%) AND rt.status = :status")
    List<RuntimeTask> findByUserIdOrDeptIdAndStatus(String userId, String deptId, TaskStatus status);
}