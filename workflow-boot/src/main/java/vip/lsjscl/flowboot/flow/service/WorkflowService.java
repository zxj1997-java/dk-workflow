package vip.lsjscl.flowboot.flow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vip.lsjscl.flowboot.flow.entity.Workflow;
import vip.lsjscl.flowboot.flow.entity.WorkflowVersion;
import vip.lsjscl.flowboot.flow.repository.WorkflowRepository;
import vip.lsjscl.flowboot.flow.repository.WorkflowVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import vip.lsjscl.flowboot.flow.dto.WorkflowCreateDTO;
import vip.lsjscl.flowboot.common.exception.BusinessException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowVersionRepository workflowVersionRepository;
    
    @Transactional
    public Workflow createWorkflow(WorkflowCreateDTO dto) {
        // 检查编码是否已存在
        if (workflowRepository.existsByCode(dto.getCode())) {
            throw new BusinessException("工作流编码已存在");
        }
        
        Workflow workflow = new Workflow();
        workflow.setName(dto.getName());
        workflow.setCode(dto.getCode());
        workflow.setCreateTime(LocalDateTime.now());
        workflow.setStatus("DRAFT");
        
        return workflowRepository.save(workflow);
    }

    public Workflow saveWorkflow(String name, String flowData, Long id) {
        Workflow workflow;
        if (id != null) {
            workflow = workflowRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("工作流不存在"));
            workflow.setName(name);
            workflow.setFlowData(flowData);
        } else {
            workflow = new Workflow();
            workflow.setName(name);
            workflow.setFlowData(flowData);
        }
        return workflowRepository.save(workflow);
    }

    public List<Workflow> findAll() {
        List<Workflow> workflows = workflowRepository.findAll();
        
        // 为每个工作流添加版本信息
        for (Workflow workflow : workflows) {
            // 获取最新版本号
            Integer currentVersion = workflowVersionRepository.findMaxVersionByWorkflowId(workflow.getId());
            workflow.setCurrentVersion(currentVersion);
            
            // 获取所有版本
            List<WorkflowVersion> versions = workflowVersionRepository.findByWorkflowIdOrderByVersionDesc(workflow.getId());
            workflow.setVersions(versions);
        }
        
        return workflows;
    }

    public Workflow findById(Long id) {
        return workflowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("工作流不存在"));
    }

    public WorkflowVersion publishWorkflow(Long workflowId) {
        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("工作流不存在"));
        
        // 获取最新版本号并加1
        Integer currentVersion = workflowVersionRepository.findMaxVersionByWorkflowId(workflowId);
        Integer newVersion = currentVersion + 1;
        
        // 创建新版本
        WorkflowVersion version = new WorkflowVersion();
        version.setWorkflowId(workflowId);
        version.setVersion(newVersion);
        version.setFlowData(workflow.getFlowData());
        
        return workflowVersionRepository.save(version);
    }

    public List<WorkflowVersion> getWorkflowVersions(Long workflowId) {
        return workflowVersionRepository.findByWorkflowIdOrderByVersionDesc(workflowId);
    }

    public WorkflowVersion getWorkflowVersion(Long workflowId, Integer version) {
        return workflowVersionRepository.findByWorkflowIdAndVersion(workflowId, version);
    }
} 