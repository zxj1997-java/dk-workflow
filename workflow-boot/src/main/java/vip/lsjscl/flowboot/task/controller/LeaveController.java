package vip.lsjscl.flowboot.task.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vip.lsjscl.flowboot.task.service.LeaveService;
import vip.lsjscl.flowboot.task.common.utils.R;
import java.util.Map;

@RestController
@RequestMapping("/workflow/leave")
public class LeaveController {
    
    @Autowired
    private LeaveService leaveService;

    /**
     * 获取我的申请列表
     */
    @GetMapping("/my-applications")
    public R myApplications(@RequestParam Map<String, Object> params) {
        return leaveService.getMyApplications(params);
    }

    /**
     * 获取待办任务列表
     */
    @GetMapping("/todo-tasks")
    public R todoTasks(@RequestParam Map<String, Object> params) {
        return leaveService.getTodoTasks(params);
    }

    /**
     * 获取已办任务列表
     */
    @GetMapping("/done-tasks")
    public R doneTasks(@RequestParam Map<String, Object> params) {
        return leaveService.getDoneTasks(params);
    }

    /**
     * 提交请假申请
     */
    @PostMapping("/submit")
    public R submit(@RequestBody Map<String, Object> params) {
        return leaveService.submitLeaveApplication(params);
    }

    /**
     * 处理请假申请
     */
    @PostMapping("/process")
    public R process(@RequestBody Map<String, Object> params) {
        return leaveService.processLeaveApplication(params);
    }
} 