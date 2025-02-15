package vip.lsjscl.flowboot.starter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vip.lsjscl.flowboot.starter.config.FlowBootProperties;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author zhangxingju
 * @date 2025/02/15
 */
@Service
@RequiredArgsConstructor
public class DeleteStrategyService {
    
    private final FlowBootProperties properties;

    public <T> void delete(JpaRepository<T, String> repository, String id) {
        if (properties.isPhysicalDelete()) {
            // 物理删除
            repository.deleteById(id);
        } else {
            // 逻辑删除 - 通过 @SQLDelete 注解实现
            repository.deleteById(id);
        }
    }
} 