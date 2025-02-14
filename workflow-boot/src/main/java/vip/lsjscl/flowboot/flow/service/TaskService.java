package vip.lsjscl.flowboot.flow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vip.lsjscl.flowboot.flow.entity.Activity;
import vip.lsjscl.flowboot.flow.entity.HistoryTask;
import vip.lsjscl.flowboot.flow.entity.RuntimeTask;
import vip.lsjscl.flowboot.flow.repository.ActivityRepository;
import vip.lsjscl.flowboot.flow.repository.HistoryTaskRepository;
import vip.lsjscl.flowboot.flow.repository.RuntimeTaskRepository;
import vip.lsjscl.flowboot.flow.repository.TransitionRepository;
import vip.lsjscl.flowboot.flow.dict.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        // 找到开始节点和结束节点
        Activity startActivity = findStartActivity(activities);
        Activity endActivity = findEndActivity(activities);

        if (startActivity == null || endActivity == null) {
            return false;
        }

        // 检查结束节点是否完成
        return tasks.stream()
                .anyMatch(task -> task.getActivity().getId().equals(endActivity.getId())
                        && task.getStatus() == TaskStatus.COMPLETED);
    }

    private Activity findStartActivity(List<Activity> activities) {
        // 找到没有入边的节点作为开始节点
        return activities.stream()
                .filter(activity -> !hasIncomingTransitions(activity))
                .findFirst()
                .orElse(null);
    }

    private Activity findEndActivity(List<Activity> activities) {
        // 找到没有出边的节点作为结束节点
        return activities.stream()
                .filter(activity -> !hasOutgoingTransitions(activity))
                .findFirst()
                .orElse(null);
    }

    private void syncToHistory(List<RuntimeTask> tasks) {
        for (RuntimeTask task : tasks) {
            // 创建历史任务记录
            HistoryTask historyTask = new HistoryTask();
            historyTask.setActivity(task.getActivity());
            historyTask.setWorkflowVersionId(task.getWorkflowVersionId());
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
} 