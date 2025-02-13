package vip.lsjscl.flowboot.flow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.lsjscl.flowboot.flow.entity.Workflow;

import java.util.Optional;

public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
    boolean existsByCode(String code);

    @Override
    Optional<Workflow> findById(Long id);
} 