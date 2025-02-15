package vip.lsjscl.flowboot.starter.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * flowboot 自动配置
 *
 * @author zhangxingju
 * @date 2025/02/15
 */
@Configuration
@EnableConfigurationProperties(FlowBootProperties.class)
public class FlowBootAutoConfiguration {
    // 可以添加其他配置
} 