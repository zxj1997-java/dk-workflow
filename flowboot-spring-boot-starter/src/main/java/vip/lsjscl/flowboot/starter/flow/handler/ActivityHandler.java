package vip.lsjscl.flowboot.starter.flow.handler;

import vip.lsjscl.flowboot.starter.flow.entity.RuntimeTask;

/**
 * 活动节点处理器接口
 * 由使用方实现此接口来处理活动节点的业务逻辑
 *
 * @author zhangxingju
 * @date 2025/02/15
 */
public interface ActivityHandler {
    
    /**
     * 处理活动节点
     * @param businessId 业务ID
     * @param task 运行时任务
     */
    void handle(String businessId, RuntimeTask task);
} 