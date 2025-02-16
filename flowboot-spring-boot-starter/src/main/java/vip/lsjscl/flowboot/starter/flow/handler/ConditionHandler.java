package vip.lsjscl.flowboot.starter.flow.handler;

import vip.lsjscl.flowboot.starter.flow.entity.Transition;

/**
 * 条件处理器接口
 * 由使用方实现此接口来处理变迁的条件判断
 *
 * @author zhangxingju
 * @date 2025/02/15
 */
public interface ConditionHandler {
    
    /**
     * 评估条件是否满足
     * @param businessId 业务ID
     * @param transition 变迁信息
     * @return 条件是否满足
     */
    boolean evaluate(String businessId, Transition transition);
} 