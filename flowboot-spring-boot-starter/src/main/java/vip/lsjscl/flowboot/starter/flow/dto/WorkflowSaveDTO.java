package vip.lsjscl.flowboot.starter.flow.dto;

import lombok.Data;

/**
 * 工作流保存 DTO
 * 用于接收前端提交的工作流保存信息
 */
@Data
public class WorkflowSaveDTO {
    private Long id;
    private String name;
    private String code;
    private String flowData;
    // 如果需要，可以添加更多字段，如版本号、描述等
} 