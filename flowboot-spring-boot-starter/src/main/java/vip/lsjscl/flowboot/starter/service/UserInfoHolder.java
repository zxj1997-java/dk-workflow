package vip.lsjscl.flowboot.starter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 用户信息持有者
 * 用于在框架内部获取当前用户信息
 *
 * @author zhangxingju
 * @date 2025/02/15
 */
@Component
@RequiredArgsConstructor
public class UserInfoHolder {
    
    private final UserInfoProvider userInfoProvider;
    
    public String getCurrentUserId() {
        return userInfoProvider != null ? userInfoProvider.getCurrentUserId() : null;
    }
    
    public String getCurrentUsername() {
        return userInfoProvider != null ? userInfoProvider.getCurrentUsername() : null;
    }
} 