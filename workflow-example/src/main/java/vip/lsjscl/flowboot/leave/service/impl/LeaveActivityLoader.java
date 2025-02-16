package vip.lsjscl.flowboot.leave.service.impl;

import org.springframework.stereotype.Service;
import vip.lsjscl.flowboot.config.SpringContextUtil;
import vip.lsjscl.flowboot.leave.entity.LeaveInfo;
import vip.lsjscl.flowboot.leave.repository.LeaveInfoRepository;
import vip.lsjscl.flowboot.starter.flow.entity.Activity;
import vip.lsjscl.flowboot.starter.flow.entity.RuntimeTask;
import vip.lsjscl.flowboot.starter.flow.entity.Transition;
import vip.lsjscl.flowboot.starter.flow.handler.ActivityHandler;
import vip.lsjscl.flowboot.starter.flow.handler.TransitionHandler;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 变迁class
 *
 * @author zhangxingju
 * @date 2025/02/16
 */
@Service
public class LeaveActivityLoader implements ActivityHandler {
    /**
     * 处理变迁
     *
     * @param businessId 业务ID
     * @param activity   活动信息
     */
    @Override
    public void handle(String businessId, Activity activity) {
        System.err.println(businessId);
        System.err.println(activity.getCode());


    }
}
