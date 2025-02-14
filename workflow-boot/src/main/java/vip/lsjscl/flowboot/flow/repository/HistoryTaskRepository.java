package vip.lsjscl.flowboot.flow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vip.lsjscl.flowboot.flow.entity.HistoryTask;
import java.util.List;

/**
 * 历史任务仓库
 *
 * @author zhangxingju
 * @date 2025/02/14
 */
@Repository
public interface HistoryTaskRepository extends JpaRepository<HistoryTask, Long> {
    // 根据业务ID查询历史任务
    List<HistoryTask> findByBusinessId(String businessId);

    // 根据人员ID查询已办任务
    @Query("SELECT ht FROM HistoryTask ht WHERE ht.activity.approvers LIKE %:userId%")
    List<HistoryTask> findByUserId(String userId);

    // 根据部门ID查询已办任务
    @Query("SELECT ht FROM HistoryTask ht WHERE ht.activity.departments LIKE %:deptId%")
    List<HistoryTask> findByDeptId(String deptId);

    // 根据人员ID或部门ID查询已办任务
    @Query("SELECT ht FROM HistoryTask ht WHERE ht.activity.approvers LIKE %:userId% OR ht.activity.departments LIKE %:deptId%")
    List<HistoryTask> findByUserIdOrDeptId(String userId, String deptId);
} 