package vip.lsjscl.flowboot.starter.flow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vip.lsjscl.flowboot.starter.flow.entity.Activity;
import vip.lsjscl.flowboot.starter.flow.entity.HistoryTask;
import vip.lsjscl.flowboot.starter.flow.entity.RuntimeTask;
import vip.lsjscl.flowboot.starter.flow.repository.ActivityRepository;
import vip.lsjscl.flowboot.starter.flow.repository.HistoryTaskRepository;
import vip.lsjscl.flowboot.starter.flow.repository.RuntimeTaskRepository;
import vip.lsjscl.flowboot.starter.flow.repository.TransitionRepository;
import vip.lsjscl.flowboot.starter.flow.dict.TaskStatus;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

/**
 * 任务服务
 *
 * @author zhangxingju
 * @date 2025/02/14
 */
@Service
public class TaskService {

    @Autowired
    private RuntimeTaskRepository runtimeTaskRepository;

    @Autowired
    private HistoryTaskRepository historyTaskRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private TransitionRepository transitionRepository;

    // 每天晚上12点执行
    @Scheduled(cron = "0 0 0 * * ?")
    public void syncCompletedTasks() {
        // 1. 获取所有不同的业务ID
        List<String> businessIds = runtimeTaskRepository.findDistinctBusinessIds();

        for (String businessId : businessIds) {
            // 2. 查询该业务ID对应的所有任务
            List<RuntimeTask> tasks = runtimeTaskRepository.findByBusinessId(businessId);
            if (tasks.isEmpty()) {
                continue;
            }

            // 获取工作流版本ID（从任务中获取）
            Long workflowVersionId = tasks.get(0).getWorkflowVersionId();

            // 3. 查询该工作流版本的所有活动
            List<Activity> activities = activityRepository.findByWorkflowVersionId(workflowVersionId);

            // 4. 检查流程是否完成（从开始到结束的闭环）
            if (isProcessCompleted(tasks, activities)) {
                // 同步到历史表
                syncToHistory(tasks);
            }
        }
    }

    private boolean isProcessCompleted(List<RuntimeTask> tasks, List<Activity> activities) {
        // 检查是否有任务被终止
        boolean isTerminated = tasks.stream()
                .anyMatch(task -> task.getStatus() == TaskStatus.TERMINATED);

        if (isTerminated) {
            return true;
        }

        // 找到开始节点和所有结束节点
        Activity startActivity = findStartActivity(activities);
        List<Activity> endActivities = findEndActivities(activities);

        if (startActivity == null || endActivities.isEmpty()) {
            return false;
        }

        // 检查是否有任何一个结束节点完成
        return tasks.stream()
                .anyMatch(task -> 
                    endActivities.stream()
                        .anyMatch(endActivity -> 
                            task.getActivity().getId().equals(endActivity.getId()) 
                            && task.getStatus() == TaskStatus.COMPLETED
                        )
                );
    }

    private Activity findStartActivity(List<Activity> activities) {
        // 找到没有入边的节点作为开始节点
        return activities.stream()
                .filter(activity -> !hasIncomingTransitions(activity))
                .findFirst()
                .orElse(null);
    }

    private List<Activity> findEndActivities(List<Activity> activities) {
        // 找到所有没有出边的节点作为结束节点
        return activities.stream()
                .filter(activity -> !hasOutgoingTransitions(activity))
                .collect(Collectors.toList());
    }

    private void syncToHistory(List<RuntimeTask> tasks) {
        for (RuntimeTask task : tasks) {
            // 创建历史任务记录
            HistoryTask historyTask = new HistoryTask();
            historyTask.setActivity(task.getActivity());
            historyTask.setWorkflowVersionId(task.getWorkflowVersionId());
            historyTask.setBusinessId(task.getBusinessId());
            historyTask.setStatus(task.getStatus());
            historyTask.setComment(task.getComment());
            historyTask.setCreateTime(task.getCreateTime());
            historyTask.setUpdateTime(LocalDateTime.now());

            // 保存历史任务
            historyTaskRepository.save(historyTask);

            // 删除运行时任务
            runtimeTaskRepository.delete(task);
        }
    }

    private boolean hasIncomingTransitions(Activity activity) {
        return !transitionRepository.findByToActivity(activity).isEmpty();
    }

    private boolean hasOutgoingTransitions(Activity activity) {
        return !transitionRepository.findByFromActivity(activity).isEmpty();
    }

    /**
     * 获取任务记录（先查运行时任务，如果没有则查历史任务）
     * @param businessId 业务ID
     * @return 任务列表
     */
    public List<RuntimeTask> getTasksByBusinessId(String businessId) {
        // 先查询运行时任务
        List<RuntimeTask> runtimeTasks = runtimeTaskRepository.findByBusinessId(businessId);
        if (!runtimeTasks.isEmpty()) {
            return runtimeTasks;
        }

        // 如果运行时任务为空，则查询历史任务并转换为RuntimeTask
        List<HistoryTask> historyTasks = historyTaskRepository.findByBusinessId(businessId);
        return historyTasks.stream()
                .map(this::convertToRuntimeTask)
                .collect(Collectors.toList());
    }

    /**
     * 将历史任务转换为运行时任务对象
     */
    private RuntimeTask convertToRuntimeTask(HistoryTask historyTask) {
        RuntimeTask runtimeTask = new RuntimeTask();
        runtimeTask.setId(historyTask.getId());
        runtimeTask.setActivity(historyTask.getActivity());
        runtimeTask.setWorkflowVersionId(historyTask.getWorkflowVersionId());
        runtimeTask.setStatus(historyTask.getStatus());
        runtimeTask.setComment(historyTask.getComment());
        runtimeTask.setCreateTime(historyTask.getCreateTime());
        runtimeTask.setUpdateTime(historyTask.getUpdateTime());
        return runtimeTask;
    }

    /**
     * 查询待办任务
     */
    public List<RuntimeTask> getTodoTasks(String userId, String deptId) {
        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(deptId)) {
            return runtimeTaskRepository.findByUserIdOrDeptIdAndStatus(userId, deptId, TaskStatus.PENDING);
        } else if (StringUtils.isNotBlank(userId)) {
            return runtimeTaskRepository.findByUserIdAndStatus(userId, TaskStatus.PENDING);
        } else if (StringUtils.isNotBlank(deptId)) {
            return runtimeTaskRepository.findByDeptIdAndStatus(deptId, TaskStatus.PENDING);
        }
        return Collections.emptyList();
    }

    /**
     * 查询已办任务
     */
    public List<HistoryTask> getDoneTasks(String userId, String deptId) {
        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(deptId)) {
            return historyTaskRepository.findByUserIdOrDeptId(userId, deptId);
        } else if (StringUtils.isNotBlank(userId)) {
            return historyTaskRepository.findByUserId(userId);
        } else if (StringUtils.isNotBlank(deptId)) {
            return historyTaskRepository.findByDeptId(deptId);
        }
        return Collections.emptyList();
    }

    /**
     * 查询所有任务（包括待办和已办）
     */
    public Map<String, Object> getAllTasks(String userId, String deptId) {
        Map<String, Object> result = new HashMap<>();
        result.put("todoTasks", getTodoTasks(userId, deptId));
        result.put("doneTasks", getDoneTasks(userId, deptId));
        return result;
    }
} 