package vip.lsjscl.flowboot.starter.flow.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.lsjscl.flowboot.starter.exception.BusinessException;
import vip.lsjscl.flowboot.starter.flow.dto.ProcessDataDto;
import vip.lsjscl.flowboot.starter.flow.entity.Activity;
import vip.lsjscl.flowboot.starter.flow.entity.RuntimeTask;
import vip.lsjscl.flowboot.starter.flow.dict.TaskStatus;
import vip.lsjscl.flowboot.starter.flow.entity.Transition;
import vip.lsjscl.flowboot.starter.flow.dict.TaskDecision;
import vip.lsjscl.flowboot.starter.flow.repository.RuntimeTaskRepository;
import vip.lsjscl.flowboot.starter.flow.repository.TransitionRepository;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 活动服务类
 * <p>
 * 提供获取下一个活动的方法，考虑当一个活动后继有多个活动时，通过变迁条件进行判断
 *
 * @author
 * @date 2025/02/13
 */
@Service
@RequiredArgsConstructor
public class ActivityService {

    private final TransitionRepository transitionRepository;

    private final RuntimeTaskRepository runtimeTaskRepository;

    /**
     * 根据决策更新办件记录的当前活动节点：
     * 如果决策为 "APPROVED"，则激活下一个活动；
     * 如果决策为 "RETURN_PREVIOUS"，则激活上一个活动；
     * 如果决策为 "RETURN_APPLICANT"，则退回到第一个活动；
     * 如果决策为 "DISAPPROVED"，则流程停止，不创建新的任务记录。
     */
    @Transactional
    public void updateCurrentTaskActivity(ProcessDataDto processDataDto) {
        String businessId = String.valueOf(processDataDto.getId());
        TaskDecision decision = processDataDto.getAction();
        RuntimeTask currentTask = runtimeTaskRepository.findByBusinessIdAndStatus(businessId, TaskStatus.PENDING)
                .orElseThrow(() -> new BusinessException("未找到业务ID为 " + businessId + " 的待办理任务记录"));

        Activity newActivity;
        switch (decision) {
            case APPROVED:
                newActivity = getNextActivity(currentTask.getActivity(), businessId);
                currentTask.setStatus(TaskStatus.COMPLETED);
                break;
            case RETURN_PREVIOUS:
                newActivity = getPreviousActivity(currentTask.getActivity());
                currentTask.setStatus(TaskStatus.RETURNED);
                break;
            case RETURN_APPLICANT:
                newActivity = getFirstActivity(currentTask.getActivity().getWorkflowVersionId());
                currentTask.setStatus(TaskStatus.RETURNED);
                break;
            case DISAPPROVED:
                // 直接将当前任务更新为终止状态，流程停止，不创建新任务
                currentTask.setStatus(TaskStatus.TERMINATED);
                currentTask.setUpdateTime(LocalDateTime.now());
                runtimeTaskRepository.save(currentTask);
                return;
            default:
                throw new BusinessException("未知决策: " + decision);
        }
        currentTask.setComment(processDataDto.getComment());
        // 完成当前任务
        currentTask.setUpdateTime(LocalDateTime.now());
        runtimeTaskRepository.save(currentTask);

        // 激活下一个活动
        activateNextActiveNode(newActivity, businessId);
    }

    /**
     * 激活下一个活动节点
     *
     * @param newActivity 新活动
     * @param businessId  企业 ID
     */
    public void activateNextActiveNode(Activity newActivity, String businessId) {
        if (newActivity != null) {
            // 创建新的待办理任务记录
            RuntimeTask newTask = new RuntimeTask();
            newTask.setActivity(newActivity);
            newTask.setBusinessId(businessId);
            newTask.setCreateTime(LocalDateTime.now());
            newTask.setUpdateTime(LocalDateTime.now());
            newTask.setStatus(TaskStatus.PENDING);
            newTask.setWorkflowVersionId(newActivity.getWorkflowVersionId());
            runtimeTaskRepository.save(newTask);
        }
    }

    /**
     * 获取上一个活动节点
     * 通过查询所有以当前活动作为目标的变迁记录，返回对应的起始活动（假设只有一条记录）。
     *
     * @param currentActivity 当前活动
     * @return 上一个活动节点，如果不存在则返回 null
     */
    public Activity getPreviousActivity(Activity currentActivity) {
        List<Transition> transitions = transitionRepository.findByToActivity(currentActivity);
        if (transitions == null || transitions.isEmpty()) {
            return null;
        }
        return transitions.get(0).getFromActivity();
    }

    /**
     * 获取第一个活动
     * <p>
     * 通过查询所有变迁记录，并找出作为起始活动（即没有其他活动指向它的活动）。
     * 如果未找到符合条件的活动，则抛出异常（可根据业务需要调整为返回 null）。
     *
     * @return 第一个活动
     */
    public Activity getFirstActivity(long workflowVersionId) {
        // 根据流程版本id查询所有变迁记录
        List<Transition> transitions = transitionRepository.findByWorkflowVersionId(workflowVersionId);
        if (transitions == null || transitions.isEmpty()) {
            return null;
        }

        // 收集所有目标活动
        Set<Activity> toActivitySet = transitions.stream()
                .map(Transition::getToActivity)
                .collect(Collectors.toSet());

        // 遍历所有起始活动，找到不在目标活动集合中的活动，即为第一个活动
        for (Transition t : transitions) {
            Activity candidate = t.getFromActivity();
            if (!toActivitySet.contains(candidate)) {
                return candidate;
            }
        }
        throw new IllegalArgumentException("Cannot determine the first activity, check workflow configuration");
    }

    /**
     * 根据当前活动和上下文条件判断获取下一个活动
     *
     * @param currentActivity 当前活动
     * @return 满足条件的下一个活动；如果没有符合条件的活动则返回 null
     */
    public Activity getNextActivity(Activity currentActivity, String businessId) {
        // 根据当前活动查询所有以当前活动为起点的变迁记录
        List<Transition> transitions = transitionRepository.findByFromActivity(currentActivity);
        if (transitions == null || transitions.isEmpty()) {
            return null;
        }
        // 如果只有一个变迁，则直接返回该变迁对应的下一个活动
        if (transitions.size() == 1) {
            return transitions.get(0).getToActivity();
        }
        else {
            // 当存在多个变迁时，根据条件判断哪个变迁符合业务要求
            for (Transition transition : transitions) {
                if (evaluateTransitionCondition(transition, businessId)) {
                    return transition.getToActivity();
                }
            }
        }
        // 如果没有满足条件的变迁，则返回 null 或根据业务需要抛出异常
        throw new IllegalArgumentException("the transition conditions are not met, check the process transition configuration");
    }

    /**
     * 简单示例：通过变迁的 conditionClass 与上下文数据进行条件判断
     * 实际场景可结合反射、策略模式或其它决策逻辑处理
     *
     * @param transition 当前变迁记录
     * @return 如果条件满足返回 true，否则返回 false
     */
    private boolean evaluateTransitionCondition(Transition transition, String businessId) {
        String conditionClass = transition.getConditionClass();
        if (conditionClass == null || conditionClass.isEmpty()) {
            return true;
        }

        try {
            int lastDotIndex = conditionClass.lastIndexOf("#");
            if (lastDotIndex == -1) {
                throw new IllegalArgumentException("Invalid condition class format");
            }

            String className = conditionClass.substring(0, lastDotIndex);
            String methodName = conditionClass.substring(lastDotIndex + 1);

            // 加载类并验证方法签名
            Class<?> clazz = Class.forName(className);
            // 明确指定参数类型
            Method method = clazz.getMethod(methodName, String.class);


            // 验证返回类型
            if (!method.getReturnType().equals(boolean.class) &&
                    !method.getReturnType().equals(Boolean.class)) {
                throw new IllegalArgumentException("Method must return boolean");
            }
            // 获取无参构造方法
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            // 如果构造方法是私有的，设置可访问
            constructor.setAccessible(true);
            // 创建实例
            Object instance = constructor.newInstance();
            // 调用静态方法并传入参数
            return (Boolean) method.invoke(instance, businessId);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("Condition class not found: " + conditionClass, e);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException("Method with String parameter not found in: " + conditionClass, e);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to invoke condition method", e);
        }
        catch (Exception e) {
            throw new RuntimeException("Unexpected error evaluating condition", e);
        }
    }

} 