package vip.lsjscl.flowboot.starter.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vip.lsjscl.flowboot.starter.service.UserInfoProvider;

/**
 * flowboot 自动配置
 *
 * @author zhangxingju
 * @date 2025/02/15
 */
@Configuration
@EnableConfigurationProperties(FlowBootProperties.class)
public class FlowBootAutoConfiguration {

    /**
     * 默认用户信息提供者
     * 当用户没有配置时使用此默认实现
     */
    @Bean
    //仅当某个特定类型的 Bean 不存在时，才创建并注册带有该注解的 Bean
    @ConditionalOnMissingBean(UserInfoProvider.class)
    public UserInfoProvider defaultUserInfoProvider() {
        return new UserInfoProvider() {
            @Override
            public String getCurrentUserId() {
                return "system";
            }

            @Override
            public String getCurrentUsername() {
                return "系统用户";
            }
        };
    }
} 