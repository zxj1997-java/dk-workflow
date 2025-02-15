package vip.lsjscl.flowboot.starter.flow.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.lsjscl.flowboot.starter.common.R;
import vip.lsjscl.flowboot.starter.flow.service.SystemService;

import java.util.List;
import java.util.Map;

/**
 * 系统数据控制器
 *
 * @author zhangxingju
 * @date 2025/02/13
 */
@RestController
@RequestMapping("/api/system")
@CrossOrigin(origins = "http://localhost:8081")
@RequiredArgsConstructor
public class SystemController {

    private final SystemService systemService;

    /**
     * 获取用户
     */
    @GetMapping("/users")
    public R getUsers() {
        List<Map<String, Object>> users = systemService.getUsers();
        return R.ok(users);
    }

    /**
     * 获取部门
     */
    @GetMapping("/departments")
    public R getDepartments() {
        List<Map<String, Object>> departments = systemService.getDepartments();
        return R.ok(departments);
    }
} 