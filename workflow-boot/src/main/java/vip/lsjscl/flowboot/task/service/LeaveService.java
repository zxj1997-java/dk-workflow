package vip.lsjscl.flowboot.task.service;

import vip.lsjscl.flowboot.task.common.utils.R;
import java.util.Map;

public interface LeaveService {
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
    R submitLeaveApplication(Map<String, Object> params);

    /**
     * 处理请假申请
     */
    R processLeaveApplication(Map<String, Object> params);
} 