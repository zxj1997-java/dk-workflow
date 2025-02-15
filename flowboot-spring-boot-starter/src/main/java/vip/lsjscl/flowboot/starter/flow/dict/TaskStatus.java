package vip.lsjscl.flowboot.starter.flow.dict;

import lombok.Getter;

/**
 * 任务状态枚举
 * 待办理 10, 已办理 20
 *
 * @author zhangxingju
 * @date 2025/02/14
 */
@Getter
public enum TaskStatus {
    PENDING(10, "待办理"),
    COMPLETED(20, "已办理"),
    TERMINATED(30, "流程终止"),
    RETURNED(40, "已退回");

    private final int code;
    private final String desc;

    TaskStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TaskStatus fromCode(int code) {
        for (TaskStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown TaskStatus code: " + code);
    }
} 