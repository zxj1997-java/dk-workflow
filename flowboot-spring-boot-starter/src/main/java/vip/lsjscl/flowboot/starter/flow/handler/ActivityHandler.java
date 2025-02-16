package vip.lsjscl.flowboot.starter.flow.handler;

import vip.lsjscl.flowboot.starter.flow.entity.Activity;

/**
 * 活动节点处理器接口
 * 由使用方实现此接口来处理活动节点的业务逻辑
 *
 * @author zhangxingju
 * @date 2025/02/15
 */
@FunctionalInterface
public interface ActivityHandler {
    
    /**
     * 处理活动节点
     * @param businessId 业务ID
     * @param activity 活动节点
     */
    void handle(String businessId, Activity activity);
} 