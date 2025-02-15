package vip.lsjscl.flowboot.starter.flow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vip.lsjscl.flowboot.starter.exception.BusinessException;
import vip.lsjscl.flowboot.starter.flow.dto.WorkflowCreateDTO;
import vip.lsjscl.flowboot.starter.flow.dto.WorkflowSaveDTO;
import vip.lsjscl.flowboot.starter.flow.entity.Workflow;
import vip.lsjscl.flowboot.starter.flow.entity.WorkflowVersion;
import vip.lsjscl.flowboot.starter.flow.service.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;
import vip.lsjscl.flowboot.starter.common.R;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
        try {
            //创建工作流
            Workflow workflow = workflowService.createWorkflow(dto);
            return R.ok(workflow);
        }
        catch (Exception e) {
            log.error("创建工作流失败", e);
            throw new BusinessException("创建工作流失败: " + e.getMessage());
        }
    }

    /**
     * 更新工作流
     *
     * @param request 工作流dto对象
     */
    @PostMapping("/update")
    public R updateWorkflow(@RequestBody WorkflowSaveDTO request) {
        Workflow saved = workflowService.saveWorkflow(request.getFlowData(), request.getId());
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
    @GetMapping("/detail")
    public R getWorkflowById(@RequestParam String id) {
        Workflow workflow = workflowService.findById(id);
        return R.ok(workflow);
    }

    /**
     * 按 code 获取工作流
     *
     * @param code 工作流编码
     */
    @GetMapping("/code")
    public R getWorkflowByCode(@RequestParam String code) {
        WorkflowVersion workflow = workflowService.findByCode(code);
        return R.ok(workflow);
    }

    /**
     * 发布工作流程
     *
     * @param id 主键
     */
    @PostMapping("/publish")
    public R publishWorkflow(@RequestParam String id) {
        WorkflowVersion version = workflowService.publishWorkflow(id);
        return R.ok(version);
    }

    /**
     * 获取工作流版本
     *
     * @param id 主键
     */
    @GetMapping("/versions")
    public R getWorkflowVersions(@RequestParam String id) {
        List<WorkflowVersion> versions = workflowService.getWorkflowVersions(id);
        return R.ok(versions);
    }

    /**
     * 获取工作流版本
     *
     * @param id      主键
     * @param version 版本Id
     */
    @GetMapping("/version")
    public R getWorkflowVersion(@RequestParam String id, @RequestParam Integer version) {
        WorkflowVersion workflowVersion = workflowService.getWorkflowVersion(id, version);
        return R.ok(workflowVersion);
    }

    /**
     * 导出工作流
     *
     * @param id 工作流ID
     */
    @GetMapping("/export")
    public R exportWorkflow(@RequestParam String id) {
        try {
            Workflow workflow = workflowService.findById(id);
            if (workflow == null) {
                throw new BusinessException("工作流不存在");
            }

            // 构建导出数据
            Map<String, Object> exportData = new HashMap<>();
            exportData.put("name", workflow.getName());
            exportData.put("code", workflow.getCode());
            exportData.put("flowData", workflow.getFlowData());

            return R.ok(exportData);
        }
        catch (Exception e) {
            log.error("导出工作流失败", e);
            throw new BusinessException("导出工作流失败: " + e.getMessage());
        }
    }

    /**
     * 删除未发布的工作流
     *
     * @param id 工作流ID
     */
    @DeleteMapping("/delete")
    public R deleteWorkflow(@RequestParam String id) {
        try {
            workflowService.deleteWorkflow(id);
            return R.ok();
        }
        catch (Exception e) {
            log.error("删除工作流失败", e);
            throw new BusinessException("删除工作流失败: " + e.getMessage());
        }
    }
}