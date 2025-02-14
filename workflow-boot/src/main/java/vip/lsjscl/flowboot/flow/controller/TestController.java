package vip.lsjscl.flowboot.flow.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.lsjscl.flowboot.flow.service.SystemService;
import vip.lsjscl.flowboot.flow.service.TaskService;
import vip.lsjscl.flowboot.leave.common.utils.R;

import java.util.List;
import java.util.Map;

/**
 * 系统数据控制器
 *
 * @author zhangxingju
 * @date 2025/02/13
 */
@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "http://localhost:8081")
@RequiredArgsConstructor
public class TestController {

    private final TaskService taskService;

    @GetMapping("/sync")
    public R getUsers() {
        taskService.syncCompletedTasks();
        return R.ok();
    }
} 