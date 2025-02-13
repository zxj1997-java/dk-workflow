package vip.lsjscl.flowboot.flow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vip.lsjscl.flowboot.common.exception.BusinessException;
import vip.lsjscl.flowboot.flow.dto.WorkflowCreateDTO;
import vip.lsjscl.flowboot.flow.dto.WorkflowSaveDTO;
import vip.lsjscl.flowboot.flow.entity.Workflow;
import vip.lsjscl.flowboot.flow.entity.WorkflowVersion;
import vip.lsjscl.flowboot.flow.service.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;
import vip.lsjscl.flowboot.leave.common.utils.R;

import java.util.List;

/**
 * 工作流定义控制器
 *
 * @author zhangxingju
 * @date 2025/02/13
 */
@RestController
@RequestMapping("/api/workflow")
@CrossOrigin(origins = "http://localhost:8081")
public class WorkflowController {

    private static final Logger log = LoggerFactory.getLogger(WorkflowController.class);

    @Autowired
    private WorkflowService workflowService;

    /**
     * 创建工作流
     *
     * @param dto DTO
     */
    @PostMapping("/create")
    public R createWorkflow(@RequestBody @Valid WorkflowCreateDTO dto) {
        Workflow workflow = workflowService.createWorkflow(dto);
        return R.ok(workflow);
    }

    /**
     * 更新工作流
     *
     * @param id      主键
     * @param request 工作流dto对象
     */
    @PostMapping("/{id}")
    public R updateWorkflow(@PathVariable Long id, @RequestBody WorkflowSaveDTO request) {
        Workflow saved = workflowService.saveWorkflow(request.getName(), request.getFlowData(), id);
        return R.ok(saved);
    }

    /**
     * 保存工作流
     *
     * @param request 工作流dto对象
     */
    @PostMapping("/save")
    public R saveWorkflow(@RequestBody WorkflowSaveDTO request) {
        Workflow saved = workflowService.saveWorkflow(request.getName(), request.getFlowData(), null);
        return R.ok(saved);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("API is working");
    }

    /**
     * 获取工作流列表
     */
    @GetMapping("/list")
    public R getWorkflowList() {
        try {
            List<Workflow> workflows = workflowService.findAll();
            return R.ok(workflows);
        }
        catch (Exception e) {
            log.error("获取工作流列表失败", e);
            throw new BusinessException("获取工作流列表失败: " + e.getMessage());
        }
    }

    /**
     * 按 ID 获取工作流
     *
     * @param id 主键
     */
    @GetMapping("/{id}")
    public R getWorkflowById(@PathVariable Long id) {
        Workflow workflow = workflowService.findById(id);
        return R.ok(workflow);
    }

    /**
     * 按 code 获取工作流
     *
     * @param code 工作流编码
     */
    @GetMapping("/code/{code}")
    public R getWorkflowById(@PathVariable String code) {
        WorkflowVersion workflow = workflowService.findByCode(code);
        return R.ok(workflow);
    }

    /**
     * 发布工作流程
     *
     * @param id 主键
     */
    @PostMapping("/{id}/publish")
    public R publishWorkflow(@PathVariable Long id) {
        WorkflowVersion version = workflowService.publishWorkflow(id);
        return R.ok(version);
    }

    /**
     * 获取工作流版本
     *
     * @param id 主键
     */
    @GetMapping("/{id}/versions")
    public R getWorkflowVersions(@PathVariable Long id) {
        List<WorkflowVersion> versions = workflowService.getWorkflowVersions(id);
        return R.ok(versions);
    }

    /**
     * 获取工作流版本
     *
     * @param id      主键
     * @param version 版本Id
     */
    @GetMapping("/{id}/version/{version}")
    public R getWorkflowVersion(@PathVariable Long id, @PathVariable Integer version) {
        WorkflowVersion workflowVersion = workflowService.getWorkflowVersion(id, version);
        return R.ok(workflowVersion);
    }
}