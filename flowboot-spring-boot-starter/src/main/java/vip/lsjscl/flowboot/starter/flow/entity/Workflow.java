package vip.lsjscl.flowboot.starter.flow.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 工作流定义
 * 描述工作流的基本信息和流程配置数据
 */
@Data
@Entity
@Table(name = "dk_workflow", indexes = {
    @Index(name = "idx_workflow_code", columnList = "code", unique = true),
    @Index(name = "idx_workflow_status", columnList = "status")
})
public class Workflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("工作流名称")
    @Column(nullable = false)
    private String name;

    @Comment("工作流编码")
    @Column(nullable = false, unique = true)
    private String code;

    @Comment("工作流定义数据，使用text类型存储")
    @Column(name = "flow_data", columnDefinition = "text")
    private String flowData;

    @Comment("创建时间")
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Comment("状态")
    @Column(nullable = false)
    private Integer status;

    @Transient
    private Integer currentVersion;

    @Transient
    @OneToMany(mappedBy = "workflowId")
    private List<WorkflowVersion> versions;

    @PrePersist
    public void prePersist() {
        createTime = LocalDateTime.now();
    }
} 