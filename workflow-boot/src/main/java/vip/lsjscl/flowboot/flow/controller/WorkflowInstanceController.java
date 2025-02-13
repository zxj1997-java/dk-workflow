package vip.lsjscl.flowboot.flow.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vip.lsjscl.flowboot.flow.entity.WorkflowInstance;
import vip.lsjscl.flowboot.flow.entity.WorkflowApproval;
import vip.lsjscl.flowboot.flow.service.WorkflowInstanceService;

import java.util.List;

@RestController
@RequestMapping("/api/workflow/instance")
@CrossOrigin(origins = "http://localhost:8081")
@RequiredArgsConstructor
public class WorkflowInstanceController {

    private final WorkflowInstanceService workflowInstanceService;

    @GetMapping("/list/{workflowId}")
    public ResponseEntity<List<WorkflowInstance>> getInstanceList(@PathVariable Long workflowId) {
        List<WorkflowInstance> instances = workflowInstanceService.getInstancesByWorkflowId(workflowId);
        return ResponseEntity.ok(instances);
    }

    @GetMapping("/{instanceId}")
    public ResponseEntity<WorkflowInstance> getInstance(@PathVariable Long instanceId) {
        WorkflowInstance instance = workflowInstanceService.getInstance(instanceId);
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