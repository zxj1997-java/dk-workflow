package vip.lsjscl.flowboot.flow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vip.lsjscl.flowboot.flow.entity.Activity;
import vip.lsjscl.flowboot.flow.entity.Transition;
import vip.lsjscl.flowboot.flow.repository.TransitionRepository;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
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
    public Activity getNextActivity(Activity currentActivity) {
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
                if (evaluateTransitionCondition(transition)) {
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
    private boolean evaluateTransitionCondition(Transition transition) {
        String conditionClass = transition.getConditionClass();
        // 如果没有设定条件，则默认符合条件
        if (conditionClass == null || conditionClass.isEmpty()) {
            return true;
        }
        //反射调用conditionClass(class.method) 返回返回值
        try {
            // 分割类名和方法名
            String[] parts = conditionClass.split("\\.");
            String className = parts[0];
            String methodName = parts[1];

            // 获取类对象
            Class<?> clazz = Class.forName(className);
            // 获取方法对象
            Method method = clazz.getMethod(methodName);

            // 判断返回类型是否为 Boolean 或 boolean
            Class<?> returnType = method.getReturnType();
            if (!Boolean.class.equals(returnType) && !boolean.class.equals(returnType)) {
                throw new IllegalArgumentException("Method return type must be Boolean or boolean");
            }

            // 调用方法并获取返回值
            return (Boolean) method.invoke(null);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // 根据其他业务规则进行判断...
    }

} 