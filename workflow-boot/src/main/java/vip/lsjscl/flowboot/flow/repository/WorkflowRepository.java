package vip.lsjscl.flowboot.flow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.lsjscl.flowboot.flow.entity.Workflow;

public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
} 