package vip.lsjscl.flowboot.flow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import vip.lsjscl.flowboot.common.exception.BusinessException;
import vip.lsjscl.flowboot.flow.dict.TaskStatus;
import vip.lsjscl.flowboot.flow.dto.WorkflowCreateDTO;
import vip.lsjscl.flowboot.flow.entity.*;
import vip.lsjscl.flowboot.flow.model.Edge;
import vip.lsjscl.flowboot.flow.model.FlowDiagram;
import vip.lsjscl.flowboot.flow.model.Node;
import vip.lsjscl.flowboot.flow.repository.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作流服务
 *
 * @author zhangxingju
 * @date 2025/02/13
 */
@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowVersionRepository workflowVersionRepository;
    private final ActivityRepository activityRepository;
    private final TransitionRepository transitionRepository;
    private final RuntimeTaskRepository runtimeTaskRepository;
    private final ActivityService activityService;
    private final ObjectMapper objectMapper = new ObjectMapper();


    /**
     * 启动工作流
     * <p>
     * 根据传入的流程版本ID获取流程中的第一个活动节点，
     * 并在 dk_runtime_task 表中创建一条运行时任务记录，
     * 记录业务ID、创建时间、更新时间等字段。
     *
     * @param workflowCode 流程编码
     * @param businessId   业务ID，用于关联外部系统业务数据
     */
    @Transactional
    public Long startWorkflow(String workflowCode, String businessId) {
        // 根据流程编码查询工作流定义
        Workflow workflow = workflowRepository.findByCode(workflowCode)
                .orElseThrow(() -> new BusinessException("未找到对应的工作流定义"));

        // 查询最新版本的工作流
        WorkflowVersion latestVersion = workflowVersionRepository
                .findByWorkflowIdOrderByVersionDesc(workflow.getId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new BusinessException("工作流未发布，请先发布工作流"));

        // 通过 ActivityService 获取第一个活动节点
        Activity firstActivity = activityService.getFirstActivity(latestVersion.getId());
        if (firstActivity == null) {
            throw new BusinessException("未找到流程中的第一个活动节点");
        }

        // 创建运行时任务记录，并设置业务ID、创建时间、更新时间等信息
        RuntimeTask runtimeTask = new RuntimeTask();
        runtimeTask.setActivity(firstActivity);
        runtimeTask.setBusinessId(businessId);
        runtimeTask.setCreateTime(LocalDateTime.now());
        runtimeTask.setUpdateTime(LocalDateTime.now());
        runtimeTask.setStatus(TaskStatus.COMPLETED);
        runtimeTask.setWorkflowVersionId(latestVersion.getId());
        runtimeTaskRepository.save(runtimeTask);

        // 获取下一个活动节点
        Activity nextActivity = activityService.getNextActivity(firstActivity, businessId);
        activityService.activateNextActiveNode(nextActivity, businessId);
        return latestVersion.getId();
    }


    public Workflow createWorkflow(WorkflowCreateDTO dto) {
        // 检查工作流编码是否已存在
        if (workflowRepository.existsByCode(dto.getCode())) {
            throw new BusinessException("工作流编码已存在");
        }
        Workflow workflow = new Workflow();
        workflow.setName(dto.getName());
        workflow.setCode(dto.getCode());
        workflow.setCreateTime(LocalDateTime.now());
        workflow.setStatus(0);
        workflow.setFlowData("");
        return workflowRepository.save(workflow);
    }

    public Workflow saveWorkflow(String flowData, Long id) {
        Workflow workflow;
        if (id != null) {
            workflow = workflowRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("工作流不存在"));
        }
        else {
            workflow = new Workflow();
        }
        workflow.setFlowData(flowData);
        return workflowRepository.save(workflow);
    }

    public List<Workflow> findAll() {
        List<Workflow> workflows = workflowRepository.findAll();
        for (Workflow workflow : workflows) {
            Integer currentVersion = workflowVersionRepository.findMaxVersionByWorkflowId(workflow.getId());
            workflow.setCurrentVersion(currentVersion);
            List<WorkflowVersion> versions = workflowVersionRepository.findByWorkflowIdOrderByVersionDesc(workflow.getId());
            workflow.setVersions(versions);
        }
        return workflows;
    }

    public Workflow findById(Long id) {
        return workflowRepository.findById(id)
                .orElseThrow(() -> new BusinessException("工作流不存在"));
    }

    public WorkflowVersion findByCode(String workflowCode) {
        // 根据流程编码查询工作流定义
        Workflow workflow = workflowRepository.findByCode(workflowCode)
                .orElseThrow(() -> new BusinessException("未找到对应的工作流定义"));

        // 查询最新版本的工作流
        WorkflowVersion latestVersion = workflowVersionRepository
                .findByWorkflowIdOrderByVersionDesc(workflow.getId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new BusinessException("工作流未发布，请先发布工作流"));
        return latestVersion;
    }

    @Transactional
    public WorkflowVersion publishWorkflow(Long id) {
        // 查找对应的工作流定义
        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new BusinessException("工作流不存在"));

        // 确定新的版本号
        List<WorkflowVersion> versions = workflowVersionRepository.findByWorkflowIdOrderByVersionDesc(id);
        int newVersion = versions.isEmpty() ? 1 : versions.get(0).getVersion() + 1;

        // 创建新的工作流版本记录
        WorkflowVersion workflowVersion = new WorkflowVersion();
        workflowVersion.setWorkflowId(id);
        workflowVersion.setVersion(newVersion);
        workflowVersion.setFlowData(workflow.getFlowData());
        workflowVersion = workflowVersionRepository.save(workflowVersion);

        // 解析存储的流程图 JSON 数据
        FlowDiagram flowDiagram;
        try {
            flowDiagram = objectMapper.readValue(workflow.getFlowData(), FlowDiagram.class);
        }
        catch (Exception e) {
            throw new BusinessException("解析流程图JSON失败: " + e.getMessage(), e);
        }

        // 用于存储生成的活动记录，方便后续建立变迁关系
        Map<String, Activity> activityMap = new HashMap<>();

        if (flowDiagram.getNodes() != null) {
            for (Node node : flowDiagram.getNodes()) {
                if ("activity".equals(node.getType())) {
                    Activity activity = new Activity();
                    activity.setNodeId(node.getId());
                    if (node.getData() != null) {
                        activity.setName(node.getData().getName());
                        activity.setCode(node.getData().getCode());
                        activity.setPageUrl(node.getData().getPageUrl());
                        activity.setAfterClass(node.getData().getAfterClass());
                        activity.setApprovers(listToString(node.getData().getApprovers()));
                        activity.setDepartments(listToString(node.getData().getDepartments()));
                        activity.setOperations(listToString(node.getData().getOperations()));
                        // 设置该活动记录所属的流程版本ID，以便后续查询
                        activity.setWorkflowVersionId(workflowVersion.getId());
                    }
                    Activity savedActivity = activityRepository.save(activity);
                    activityMap.put(node.getId(), savedActivity);
                }
            }
        }

        // 遍历连线数据，生成变迁记录（仅针对已生成活动的节点）
        if (flowDiagram.getEdges() != null) {
            for (Edge edge : flowDiagram.getEdges()) {
                Activity fromActivity = activityMap.get(edge.getSource());
                Activity toActivity = activityMap.get(edge.getTarget());
                if (fromActivity != null && toActivity != null) {
                    Transition transition = new Transition();
                    transition.setFromActivity(fromActivity);
                    transition.setToActivity(toActivity);
                    if (edge.getData() != null) {
                        transition.setName(edge.getData().getName());
                        transition.setCode(edge.getData().getCode());
                        transition.setConditionClass(edge.getData().getConditionClass());
                        transition.setAfterClass(edge.getData().getAfterClass());
                    }
                    // 设置该变迁记录所属的流程版本ID，以便后续查询
                    transition.setWorkflowVersionId(workflowVersion.getId());
                    transitionRepository.save(transition);
                }
            }
        }

        // 更新工作流状态为已发布
        workflow.setStatus(1);
        workflowRepository.save(workflow);

        return workflowVersion;
    }

    public List<WorkflowVersion> getWorkflowVersions(Long workflowId) {
        return workflowVersionRepository.findByWorkflowIdOrderByVersionDesc(workflowId);
    }

    public WorkflowVersion getWorkflowVersion(Long workflowId, Integer version) {
        List<WorkflowVersion> versions = workflowVersionRepository.findByWorkflowIdOrderByVersionDesc(workflowId);
        return versions.stream().filter(v -> v.getVersion().equals(version))
                .findFirst()
                .orElseThrow(() -> new BusinessException("工作流版本不存在"));
    }

    // 辅助方法：将 List 转为逗号分隔字符串
    private String listToString(List<String> list) {
        return CollectionUtils.isEmpty(list) ? "" : String.join(",", list);
    }


}