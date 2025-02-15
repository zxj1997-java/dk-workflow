package vip.lsjscl.flowboot.starter.flow.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;
import vip.lsjscl.flowboot.starter.flow.dict.TaskStatus;

import java.time.LocalDateTime;

/**
 * 历史任务
 * 记录已完成的任务信息，用于流程追踪和日志审计
 * @author 15331
 */
@Data
@Entity
@Table(name = "dk_history_task", indexes = {
    @Index(name = "idx_history_task_business_id", columnList = "business_id"),
    @Index(name = "idx_history_task_workflow_version", columnList = "workflow_version_id"),
    @Index(name = "idx_history_task_create_time", columnList = "create_time")
})
public class HistoryTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("业务ID")
    @Column(name = "business_id")
    private String businessId;

    @Comment("关联到相应的活动")
    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @Comment("流程版本ID")
    @Column(name = "workflow_version_id")
    private Long workflowVersionId;

    @Comment("创建时间")
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Comment("更新时间")
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Comment("任务状态，如待办理(10)或已办理(20)")
    @Column(name = "status")
    private TaskStatus status;

    @Comment("审批意见")
    @Column(name = "comment")
    private String comment;
} 