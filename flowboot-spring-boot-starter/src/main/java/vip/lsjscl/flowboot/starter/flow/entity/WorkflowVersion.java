package vip.lsjscl.flowboot.starter.flow.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

/**
 * 工作流版本
 * 用于管理工作流的不同版本记录
 *
 * @author zhangxingju
 * @date 2025/02/15
 */
@Data
@Entity
@Table(name = "dk_workflow_version", indexes = {
        @Index(name = "idx_workflow_version_workflow_id", columnList = "workflow_id"),
        @Index(name = "idx_workflow_version_version", columnList = "version")
})
public class WorkflowVersion extends BaseEntity {

    @Comment("工作流ID，不能为空")
    @Column(nullable = false)
    private String workflowId;

    @Comment("版本号，不能为空")
    @Column(nullable = false)
    private Integer version;

    @Comment("工作流定义数据，使用text类型存储")
    @Column(name = "flow_data", columnDefinition = "text")
    private String flowData;
}