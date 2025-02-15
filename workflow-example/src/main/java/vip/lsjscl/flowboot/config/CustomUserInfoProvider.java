package vip.lsjscl.flowboot.config;

import org.springframework.stereotype.Component;
import vip.lsjscl.flowboot.starter.service.UserInfoProvider;

import java.util.UUID;

/**
 * 自定义用户信息提供程序
 *
 * @author zhangxingju
 * @date 2025/02/15
 */
@Component
public class CustomUserInfoProvider implements UserInfoProvider {
    String userId = UUID.randomUUID().toString();
    String userName = "张三";

    @Override
    public String getCurrentUserId() {
        // 实现获取当前用户ID的逻辑
        // 例如从 Session、Token 或者其他地方获取

        System.err.println(userId);
        return userId;
    }

    @Override
    public String getCurrentUsername() {
        // 实现获取当前用户名的逻辑
        return userName;
    }
} 