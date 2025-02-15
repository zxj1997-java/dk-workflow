package vip.lsjscl.flowboot.starter.flow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vip.lsjscl.flowboot.starter.flow.entity.Workflow;

import java.util.Optional;

/**
 * 工作流存储库
 *
 * @author zhangxingju
 * @date 2025/02/14
 */
@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
    Optional<Workflow> findByCode(String code);

    boolean existsByCode(String code);

    @Override
    Optional<Workflow> findById(Long id);
} 