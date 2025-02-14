package vip.lsjscl.flowboot.flow.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

/**
 * 工作流版本
 *
 * @author zhangxingju
 * @date 2025/02/13
 */
@Data
@Entity
@Table(name = "dk_workflow_version", indexes = {
    @Index(name = "idx_workflow_version_workflow_id", columnList = "workflow_id"),
    @Index(name = "idx_workflow_version_version", columnList = "version")
})
public class WorkflowVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Comment("工作流ID，不能为空")
    private Long workflowId;

    @Column(nullable = false)
    @Comment("版本号，不能为空")
    private Integer version;

    @Comment("工作流定义数据，使用text类型存储")
    @Column(name = "flow_data", columnDefinition = "text")
    private String flowData;

    private LocalDateTime createTime;

    @PrePersist
    public void prePersist() {
        createTime = LocalDateTime.now();
    }
} 