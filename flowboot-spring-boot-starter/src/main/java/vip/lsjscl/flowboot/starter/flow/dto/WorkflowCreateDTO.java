package vip.lsjscl.flowboot.starter.flow.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 工作流创建 DTO
 *
 * @author zhangxingju
 * @date 2025/02/14
 */
@Data
public class WorkflowCreateDTO {
    @NotBlank(message = "工作流名称不能为空")
    private String name;

    @NotBlank(message = "工作流编码不能为空")
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "编码只能包含字母、数字和下划线")
    private String code;
} 