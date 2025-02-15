package vip.lsjscl.flowboot.starter.service;

/**
 * 用户信息提供者接口
 * 由使用方实现此接口来提供当前用户信息
 *
 * @author zhangxingju
 * @date 2025/02/15
 */
public interface UserInfoProvider {
    
    /**
     * 获取当前用户ID
     */
    String getCurrentUserId();
    
    /**
     * 获取当前用户名称
     */
    String getCurrentUsername();
} 