package vip.lsjscl.flowboot.starter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * flow-boot 属性
 *
 * @author zhangxingju
 * @date 2025/02/15
 */
@Data
@ConfigurationProperties(prefix = "flow-boot")
public class FlowBootProperties {
    
    /**
     * 是否启用物理删除,默认为false(逻辑删除)
     */
    private boolean physicalDelete = false;
} 