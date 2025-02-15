package vip.lsjscl.flowboot.starter.flow.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;
import vip.lsjscl.flowboot.starter.flow.converter.JsonConverter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 工作流定义
 * 描述工作流的基本信息和流程配置数据
 *
 * @author zhangxingju
 * @date 2025/02/15
 */
@Data
@Entity
@Table(name = "dk_workflow", indexes = {
        @Index(name = "idx_workflow_code", columnList = "code", unique = true),
        @Index(name = "idx_workflow_status", columnList = "status")
})
public class Workflow extends BaseEntity {

    @Comment("工作流名称")
    @Column(nullable = false)
    private String name;

    @Comment("工作流编码")
    @Column(nullable = false, unique = true)
    private String code;

    @Comment("工作流定义数据")
    @Column(name = "flow_data", columnDefinition = "text")
    @Convert(converter = JsonConverter.class)
    private Object flowData;

    @Transient
    @OneToMany(mappedBy = "workflowId")
    private List<WorkflowVersion> versions;

    @Comment("状态")
    @Column(nullable = false)
    private Integer status;

    @Transient
    private Integer currentVersion;
} 