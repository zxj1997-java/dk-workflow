package vip.lsjscl.flowboot.flow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.lsjscl.flowboot.flow.entity.HistoryTask;
import java.util.List;

/**
 * 历史任务仓库
 *
 * @author zhangxingju
 * @date 2025/02/14
 */
public interface HistoryTaskRepository extends JpaRepository<HistoryTask, Long> {
    // 根据业务ID查询历史任务
    List<HistoryTask> findByBusinessId(String businessId);
} 