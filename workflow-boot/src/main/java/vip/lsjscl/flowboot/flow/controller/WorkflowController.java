package vip.lsjscl.flowboot.flow.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vip.lsjscl.flowboot.flow.dto.WorkflowSaveDTO;
import vip.lsjscl.flowboot.flow.entity.Workflow;
import vip.lsjscl.flowboot.flow.entity.WorkflowVersion;
import vip.lsjscl.flowboot.flow.service.WorkflowService;

import java.util.List;

@RestController
@RequestMapping("/api/workflow")
@CrossOrigin(origins = "http://localhost:8081")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping("/{id}")
    public ResponseEntity<Workflow> updateWorkflow(@PathVariable Long id, @RequestBody WorkflowSaveDTO request) {
        Workflow saved = workflowService.saveWorkflow(request.getName(), request.getFlowData(), id);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/save")
    public ResponseEntity<Workflow> saveWorkflow(@RequestBody WorkflowSaveDTO request) {
        Workflow saved = workflowService.saveWorkflow(request.getName(), request.getFlowData(), null);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("API is working");
    }

    @GetMapping("/list")
    public ResponseEntity<List<Workflow>> getWorkflowList() {
        List<Workflow> workflows = workflowService.findAll();
        return ResponseEntity.ok(workflows);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Workflow> getWorkflowById(@PathVariable Long id) {
        Workflow workflow = workflowService.findById(id);
        return ResponseEntity.ok(workflow);
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<WorkflowVersion> publishWorkflow(@PathVariable Long id) {
        WorkflowVersion version = workflowService.publishWorkflow(id);
        return ResponseEntity.ok(version);
    }

    @GetMapping("/{id}/versions")
    public ResponseEntity<List<WorkflowVersion>> getWorkflowVersions(@PathVariable Long id) {
        List<WorkflowVersion> versions = workflowService.getWorkflowVersions(id);
        return ResponseEntity.ok(versions);
    }

    @GetMapping("/{id}/version/{version}")
    public ResponseEntity<WorkflowVersion> getWorkflowVersion(
            @PathVariable Long id,
            @PathVariable Integer version
    ) {
        WorkflowVersion workflowVersion = workflowService.getWorkflowVersion(id, version);
        return ResponseEntity.ok(workflowVersion);
    }
}