package vip.lsjscl.flowboot.flow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.lsjscl.flowboot.flow.entity.HistoryTask;

/**
 * 历史任务仓库
 *
 * @author zhangxingju
 * @date 2025/02/14
 */
public interface HistoryTaskRepository extends JpaRepository<HistoryTask, Long> {
} 