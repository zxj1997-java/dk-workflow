package vip.lsjscl.flowboot.leave.service;

import vip.lsjscl.flowboot.starter.common.R;
import vip.lsjscl.flowboot.leave.entity.LeaveInfo;

import java.util.Map;

/**
 * 请假 服务
 *
 * @author zhangxingju
 * @date 2025/02/15
 */
public interface LeaveInfoService {
    /**
     * 获取我的申请列表
     */
    R getMyApplications(Map<String, Object> params);

    /**
     * 获取待办任务列表
     */
    R getTodoTasks(Map<String, Object> params);

    /**
     * 获取已办任务列表
     */
    R getDoneTasks(Map<String, Object> params);

    /**
     * 提交请假申请
     */
    R submitLeaveApplication(LeaveInfo leaveInfo);

    R saveOrUpdate(LeaveInfo leaveInfo);

    R getLeaveDetail(Long id);

    R deleteLeaveApplication(Long id);
}