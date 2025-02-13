package vip.lsjscl.flowboot.flow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SystemService {

    public List<Map<String, Object>> getUsers() {
        // TODO: 实际项目中应该从数据库或其他服务获取用户列表
        List<Map<String, Object>> users = new ArrayList<>();
        users.add(Map.of("value", "1", "label", "张三"));
        users.add(Map.of("value", "2", "label", "李四"));
        users.add(Map.of("value", "3", "label", "王五"));
        return users;
    }

    public List<Map<String, Object>> getDepartments() {
        // TODO: 实际项目中应该从数据库或其他服务获取部门列表
        List<Map<String, Object>> departments = new ArrayList<>();
        departments.add(Map.of("value", "1", "label", "研发部"));
        departments.add(Map.of("value", "2", "label", "产品部"));
        departments.add(Map.of("value", "3", "label", "运营部"));
        return departments;
    }
} 