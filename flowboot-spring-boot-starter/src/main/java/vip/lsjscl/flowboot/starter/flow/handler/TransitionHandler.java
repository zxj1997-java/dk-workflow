package vip.lsjscl.flowboot.starter.flow.handler;

import vip.lsjscl.flowboot.starter.flow.entity.Transition;

/**
 * 变迁处理器接口
 * 由使用方实现此接口来处理变迁的业务逻辑
 *
 * @author zhangxingju
 * @date 2025/02/15
 */
@FunctionalInterface
public interface TransitionHandler {

    /**
     * 处理变迁
     *
     * @param businessId 业务ID
     * @param transition 变迁信息
     */
    boolean handler(String businessId, Transition transition);
} 