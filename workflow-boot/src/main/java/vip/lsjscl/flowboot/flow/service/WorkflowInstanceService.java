package vip.lsjscl.flowboot.flow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vip.lsjscl.flowboot.flow.entity.WorkflowInstance;
import vip.lsjscl.flowboot.flow.entity.WorkflowApproval;
import vip.lsjscl.flowboot.flow.repository.WorkflowInstanceRepository;
import vip.lsjscl.flowboot.flow.repository.WorkflowApprovalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkflowInstanceService {

    private final WorkflowInstanceRepository workflowInstanceRepository;
    private final WorkflowApprovalRepository workflowApprovalRepository;

    public List<WorkflowInstance> getInstancesByWorkflowId(Long workflowId) {
        return workflowInstanceRepository.findByWorkflowIdOrderByCreateTimeDesc(workflowId);
    }

    public WorkflowInstance getInstance(Long instanceId) {
        return workflowInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new RuntimeException("流程实例不存在"));
    }

    public List<WorkflowApproval> getApprovalRecords(Long instanceId) {
        return workflowApprovalRepository.findByInstanceIdOrderByCreateTimeDesc(instanceId);
    }

    public WorkflowInstance createInstance(WorkflowInstance instance) {
        return workflowInstanceRepository.save(instance);
    }

    public WorkflowApproval addApprovalRecord(WorkflowApproval approval) {
        return workflowApprovalRepository.save(approval);
    }

    public WorkflowInstance updateInstance(WorkflowInstance instance) {
        return workflowInstanceRepository.save(instance);
    }
} 