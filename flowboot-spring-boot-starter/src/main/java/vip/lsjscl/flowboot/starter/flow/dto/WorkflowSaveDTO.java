package vip.lsjscl.flowboot.starter.flow.dto;

import lombok.Data;

/**
 * 工作流保存 DTO
 * 用于接收前端提交的工作流保存信息
 *
 * @author zhangxingju
 * @date 2025/02/15
 */
@Data
public class WorkflowSaveDTO {
    private String id;
    private String name;
    private String code;
    private String flowData;
    // 如果需要，可以添加更多字段，如版本号、描述等
} 