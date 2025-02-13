package vip.lsjscl.flowboot.flow.dict;

/**
 * 任务决策枚举：
 * APPROVED - 通过，激活下一个活动；
 * RETURN_PREVIOUS - 退回上一步，激活上一个活动；
 * RETURN_APPLICANT - 退回申请人，激活第一个活动；
 * DISAPPROVED - 不通过，流程停止，不创建新的任务记录。
 */
public enum TaskDecision {
    APPROVED,
    RETURN_PREVIOUS,
    RETURN_APPLICANT,
    DISAPPROVED
} 