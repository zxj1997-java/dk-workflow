package vip.lsjscl.flowboot.flow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.lsjscl.flowboot.flow.entity.Workflow;

import java.util.Optional;

/**
 * 工作流存储库
 *
 * @author zhangxingju
 * @date 2025/02/14
 */
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
    Optional<Workflow> findByCode(String code);

    boolean existsByCode(String code);

    @Override
    Optional<Workflow> findById(Long id);
} 