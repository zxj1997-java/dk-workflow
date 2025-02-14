package vip.lsjscl.flowboot.flow.dict;

import lombok.Getter;

/**
 * 任务决策枚举：
 * APPROVED - 通过，激活下一个活动；
 * RETURN_PREVIOUS - 退回上一步，激活上一个活动；
 * RETURN_APPLICANT - 退回申请人，激活第一个活动；
 * DISAPPROVED - 不通过，流程停止，不创建新的任务记录。
 * @author 15331
 */
@Getter
public enum TaskDecision {

    APPROVED("通过"),
    RETURN_PREVIOUS("退回上一步"),
    RETURN_APPLICANT("退回申请人"),
    DISAPPROVED("不通过");

    private final String operateName;

    TaskDecision(String operateName) {
        this.operateName = operateName;
    }

    //通过name获取operateName
    public static String getOperateNameByName(String name) {
        try {
            return TaskDecision.valueOf(name).getOperateName();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

} 