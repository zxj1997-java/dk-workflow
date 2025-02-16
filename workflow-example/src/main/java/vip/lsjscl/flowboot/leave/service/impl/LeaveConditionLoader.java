package vip.lsjscl.flowboot.leave.service.impl;

import org.springframework.stereotype.Service;
import vip.lsjscl.flowboot.config.SpringContextUtil;
import vip.lsjscl.flowboot.leave.entity.LeaveInfo;
import vip.lsjscl.flowboot.leave.repository.LeaveInfoRepository;
import vip.lsjscl.flowboot.starter.flow.entity.Transition;
import vip.lsjscl.flowboot.starter.flow.handler.ConditionHandler;
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
public class LeaveConditionLoader implements ConditionHandler {
    /**
     * 处理变迁
     *
     * @param businessId 业务ID
     * @param transition 变迁信息
     */
    @Override
    public boolean evaluate(String businessId, Transition transition) {
        System.err.println(businessId);
        System.err.println(transition.getCode());
        //从Spring容器中获取Bean
        LeaveInfoRepository leaveInfoRepository = SpringContextUtil.getBean(LeaveInfoRepository.class);
        Optional<LeaveInfo> optionalLeave = leaveInfoRepository.findById(businessId);
        LeaveInfo leaveInfo = optionalLeave.get();
        Date startDate = leaveInfo.getStartDate();
        Date endDate = leaveInfo.getEndDate();
        long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
        long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        if (diffInDays < 2 && "xy2".equals(transition.getCode())) {
            return true;
        }
        if (diffInDays >= 2 && "dy2".equals(transition.getCode())) {
            return true;
        }
        return false;
    }
}
