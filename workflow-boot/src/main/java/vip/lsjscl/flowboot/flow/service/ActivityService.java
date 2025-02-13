package vip.lsjscl.flowboot.flow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vip.lsjscl.flowboot.flow.entity.Activity;
import vip.lsjscl.flowboot.flow.entity.Transition;
import vip.lsjscl.flowboot.flow.repository.TransitionRepository;

import java.util.List;
import java.util.Map;

/**
 * 活动服务类
 *
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
     * 根据当前活动和上下文条件判断获取下一个活动
     *
     * @param currentActivity 当前活动
     * @param context         业务上下文，用于判断变迁条件（比如审批结果、参数等等）
     * @return 满足条件的下一个活动；如果没有符合条件的活动则返回 null
     */
    public Activity getNextActivity(Activity currentActivity, Map<String, Object> context) {
        // 根据当前活动查询所有以当前活动为起点的变迁记录
        List<Transition> transitions = transitionRepository.findByFromActivity(currentActivity);
        if (transitions == null || transitions.isEmpty()) {
            return null;
        }
        // 如果只有一个变迁，则直接返回该变迁对应的下一个活动
        if (transitions.size() == 1) {
            return transitions.get(0).getToActivity();
        } else {
            // 当存在多个变迁时，根据条件判断哪个变迁符合业务要求
            for (Transition transition : transitions) {
                if (evaluateTransitionCondition(transition, context)) {
                    return transition.getToActivity();
                }
            }
        }
        // 如果没有满足条件的变迁，则返回 null 或根据业务需要抛出异常
        return null;
    }

    /**
     * 简单示例：通过变迁的 conditionClass 与上下文数据进行条件判断
     * 实际场景可结合反射、策略模式或其它决策逻辑处理
     *
     * @param transition 当前变迁记录
     * @param context    上下文数据（例如审批结果等）
     * @return 如果条件满足返回 true，否则返回 false
     */
    private boolean evaluateTransitionCondition(Transition transition, Map<String, Object> context) {
        String conditionClass = transition.getConditionClass();
        // 如果没有设定条件，则默认符合条件
        if (conditionClass == null || conditionClass.isEmpty()) {
            return true;
        }
        // 示例逻辑：假设上下文中有 "approve" 参数，值为 Boolean 且为 true 则认为条件满足
        Object approveResult = context.get("approve");
        if (approveResult instanceof Boolean && (Boolean) approveResult) {
            return true;
        }
        // 根据其他业务规则进行判断...
        return false;
    }
} 