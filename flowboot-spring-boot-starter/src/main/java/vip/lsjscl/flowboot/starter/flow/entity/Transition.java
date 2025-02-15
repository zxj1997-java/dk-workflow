package vip.lsjscl.flowboot.starter.flow.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

/**
 * 变迁
 * 描述工作流中两个活动之间的转换关系
 */
@Data
@Entity
@Table(name = "dk_transition", indexes = {
        @Index(name = "idx_history_task_workflow_version", columnList = "workflow_version_id")
})
public class Transition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("起始活动")
    @ManyToOne
    @JoinColumn(name = "from_activity_id")
    private Activity fromActivity;

    @Comment("目标活动")
    @ManyToOne
    @JoinColumn(name = "to_activity_id")
    private Activity toActivity;

    @Comment("变迁名称")
    @Column(name = "name")
    private String name;

    @Comment("变迁编码")
    @Column(name = "code")
    private String code;

    @Comment("条件处理类")
    @Column(name = "condition_class")
    private String conditionClass;

    @Comment("后置处理类")
    @Column(name = "after_class")
    private String afterClass;

    @Comment("流程版本ID")
    @Column(name = "workflow_version_id")
    private Long workflowVersionId;
} 