package vip.lsjscl.flowboot.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 * 字符编码配置
 *
 * @author zhangxingju
 * @date 2025/02/15
 */
@Configuration
public class CharacterEncodingConfig {
    @Bean
    public FilterRegistrationBean<CharacterEncodingFilter> encodingFilterBean() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        // 强制对请求和响应均使用 UTF-8
        filter.setForceEncoding(true);
        FilterRegistrationBean<CharacterEncodingFilter> registrationBean = new FilterRegistrationBean<>(filter);
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }
} 