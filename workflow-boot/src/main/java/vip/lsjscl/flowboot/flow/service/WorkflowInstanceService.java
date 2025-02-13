package vip.lsjscl.flowboot.flow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vip.lsjscl.flowboot.flow.entity.WorkflowInstance;
import vip.lsjscl.flowboot.flow.entity.WorkflowApproval;
import vip.lsjscl.flowboot.flow.repository.WorkflowInstanceRepository;
import vip.lsjscl.flowboot.flow.repository.WorkflowApprovalRepository;
import vip.lsjscl.flowboot.common.exception.BusinessException;

import java.util.List;

/**
 * 工作流实例服务
 *
 * @author zhangxingju
 * @date 2025/02/13
 */
@Service
@RequiredArgsConstructor
public class WorkflowInstanceService {

    private final WorkflowInstanceRepository workflowInstanceRepository;
    private final WorkflowApprovalRepository workflowApprovalRepository;

    public List<WorkflowInstance> findByWorkflowId(Long workflowId) {
        return workflowInstanceRepository.findByWorkflowIdOrderByCreateTimeDesc(workflowId);
    }

    public WorkflowInstance findById(Long id) {
        return workflowInstanceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("工作流实例不存在"));
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