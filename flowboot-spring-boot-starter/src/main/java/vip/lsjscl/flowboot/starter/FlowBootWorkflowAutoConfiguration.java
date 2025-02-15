package vip.lsjscl.flowboot.starter;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Flowboot 自动配置类，自动扫描并装配工作流相关组件。
 * 当其它项目引入本 starter 后，将自动注册并生效。
 * @author 15331
 */
@Configuration
@ComponentScan(basePackages = "vip.lsjscl.flowboot")
public class FlowBootWorkflowAutoConfiguration {
    // 如果需要，可以在此处进一步配置 Bean，例如自定义属性绑定等
} 