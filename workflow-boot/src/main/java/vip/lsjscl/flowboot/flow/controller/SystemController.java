package vip.lsjscl.flowboot.flow.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vip.lsjscl.flowboot.flow.service.SystemService;

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
    public ResponseEntity<List<Map<String, Object>>> getUsers() {
        List<Map<String, Object>> users = systemService.getUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * 获取部门
     */
    @GetMapping("/departments")
    public ResponseEntity<List<Map<String, Object>>> getDepartments() {
        List<Map<String, Object>> departments = systemService.getDepartments();
        return ResponseEntity.ok(departments);
    }
} 