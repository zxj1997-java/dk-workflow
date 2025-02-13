package vip.lsjscl.flowboot.flow.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vip.lsjscl.flowboot.flow.entity.WorkflowInstance;
import vip.lsjscl.flowboot.flow.entity.WorkflowApproval;
import vip.lsjscl.flowboot.flow.service.WorkflowInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vip.lsjscl.flowboot.common.exception.BusinessException;

import java.util.List;

/**
 * 工作流实例控制器
 *
 * @author zhangxingju
 * @date 2025/02/13
 */
@RestController
@RequestMapping("/api/workflow/instance")
@CrossOrigin(origins = "http://localhost:8081")
@RequiredArgsConstructor
public class WorkflowInstanceController {

    private static final Logger log = LoggerFactory.getLogger(WorkflowInstanceController.class);

    private final WorkflowInstanceService workflowInstanceService;

    /**
     * 获取工作流实例列表
     *
     * @param workflowId 工作流ID
     */
    @GetMapping("/list/{workflowId}")
    public ResponseEntity<List<WorkflowInstance>> getWorkflowInstances(@PathVariable Long workflowId) {
        try {
            List<WorkflowInstance> instances = workflowInstanceService.findByWorkflowId(workflowId);
            return ResponseEntity.ok(instances);
        } catch (Exception e) {
            log.error("获取工作流实例列表失败", e);
            throw new BusinessException("获取工作流实例列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取工作流实例详情
     *
     * @param instanceId 实例ID
     */
    @GetMapping("/{instanceId}")
    public ResponseEntity<WorkflowInstance> getWorkflowInstance(@PathVariable Long instanceId) {
        WorkflowInstance instance = workflowInstanceService.findById(instanceId);
        return ResponseEntity.ok(instance);
    }

    @GetMapping("/{instanceId}/approvals")
    public ResponseEntity<List<WorkflowApproval>> getApprovalRecords(@PathVariable Long instanceId) {
        List<WorkflowApproval> records = workflowInstanceService.getApprovalRecords(instanceId);
        return ResponseEntity.ok(records);
    }

    @PostMapping("/create")
    public ResponseEntity<WorkflowInstance> createInstance(@RequestBody WorkflowInstance instance) {
        WorkflowInstance created = workflowInstanceService.createInstance(instance);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/{instanceId}/approve")
    public ResponseEntity<WorkflowApproval> approve(
            @PathVariable Long instanceId,
            @RequestBody WorkflowApproval approval
    ) {
        approval.setInstanceId(instanceId);
        WorkflowApproval created = workflowInstanceService.addApprovalRecord(approval);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{instanceId}")
    public ResponseEntity<WorkflowInstance> updateInstance(
            @PathVariable Long instanceId,
            @RequestBody WorkflowInstance instance
    ) {
        instance.setId(instanceId);
        WorkflowInstance updated = workflowInstanceService.updateInstance(instance);
        return ResponseEntity.ok(updated);
    }
} 