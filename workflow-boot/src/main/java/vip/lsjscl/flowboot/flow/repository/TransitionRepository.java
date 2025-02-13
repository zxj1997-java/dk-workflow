package vip.lsjscl.flowboot.flow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.lsjscl.flowboot.flow.entity.Transition;

public interface TransitionRepository extends JpaRepository<Transition, Long> {
} 