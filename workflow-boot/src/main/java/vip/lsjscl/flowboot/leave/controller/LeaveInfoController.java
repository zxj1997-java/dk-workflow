package vip.lsjscl.flowboot.leave.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vip.lsjscl.flowboot.leave.common.utils.R;
import vip.lsjscl.flowboot.leave.entity.LeaveInfo;
import vip.lsjscl.flowboot.leave.service.LeaveInfoService;

import java.util.Map;

/**
 * 请假信息控制器
 *
 * @author zhangxingju
 * @date 2025/02/13
 */
@RestController
@RequestMapping("/workflow/leave")
@CrossOrigin(origins = "http://localhost:8081")
public class LeaveInfoController {

    @Autowired
    private LeaveInfoService leaveService;

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
     * 保存草稿
     */
    @PostMapping("/save-draft")
    public R saveDraft(@RequestBody LeaveInfo leaveInfo) {
        leaveInfo.setStatus("DRAFT");
        return leaveService.saveOrUpdate(leaveInfo);
    }

    /**
     * 提交申请
     */
    @PostMapping("/submit")
    public R submit(@RequestBody LeaveInfo leaveInfo) {
        leaveInfo.setStatus("PENDING");
        return leaveService.submitLeaveApplication(leaveInfo);
    }

    /**
     * 获取申请详情
     */
    @GetMapping("/detail/{id}")
    public R getDetail(@PathVariable("id") Long id) {
        return leaveService.getLeaveDetail(id);
    }

    /**
     * 处理申请
     */
    @PostMapping("/process")
    public R process(@RequestBody Map<String, Object> params) {
        return leaveService.processLeaveApplication(params);
    }

    /**
     * 删除申请
     */
    @DeleteMapping("/{id}")
    public R delete(@PathVariable("id") Long id) {
        return leaveService.deleteLeaveApplication(id);
    }
} 