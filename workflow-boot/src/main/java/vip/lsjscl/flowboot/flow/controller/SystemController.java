package vip.lsjscl.flowboot.flow.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vip.lsjscl.flowboot.flow.service.SystemService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
@CrossOrigin(origins = "http://localhost:8081")
@RequiredArgsConstructor
public class SystemController {

    private final SystemService systemService;

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getUsers() {
        List<Map<String, Object>> users = systemService.getUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/departments")
    public ResponseEntity<List<Map<String, Object>>> getDepartments() {
        List<Map<String, Object>> departments = systemService.getDepartments();
        return ResponseEntity.ok(departments);
    }
} 