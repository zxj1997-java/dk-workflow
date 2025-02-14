package vip.lsjscl.flowboot.flow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.lsjscl.flowboot.flow.entity.Activity;

/**
 * 活动存储库
 *
 * @author zhangxingju
 * @date 2025/02/14
 */
public interface ActivityRepository extends JpaRepository<Activity, Long> {
} 