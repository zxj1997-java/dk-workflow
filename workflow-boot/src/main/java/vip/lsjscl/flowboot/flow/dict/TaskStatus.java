package vip.lsjscl.flowboot.flow.dict;

import lombok.Getter;

/**
 * 任务状态枚举
 * 待办理 10, 已办理 20
 * @author 15331
 */
@Getter
public enum TaskStatus {
    PENDING(10, "待办理"),
    COMPLETED(20, "已办理"),
    TERMINATED(30, "流程停止");

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