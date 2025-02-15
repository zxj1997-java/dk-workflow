package vip.lsjscl.flowboot.starter.flow.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;
import vip.lsjscl.flowboot.starter.flow.dict.TaskStatus;

import java.time.LocalDateTime;

/**
 * 运行时任务
 * 表示当前正在待办状态的任务信息
 */
@Data
@Entity
@Table(name = "dk_runtime_task", indexes = {
    @Index(name = "idx_runtime_task_business_id", columnList = "business_id"),
    @Index(name = "idx_runtime_task_status", columnList = "status"),
    @Index(name = "idx_runtime_task_business_status", columnList = "business_id,status"),
    @Index(name = "idx_runtime_task_workflow_version", columnList = "workflow_version_id")
})
public class RuntimeTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("关联到相应的活动")
    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @Comment("流程版本ID")
    @Column(name = "workflow_version_id")
    private Long workflowVersionId;

    @Comment("业务ID")
    @Column(name = "business_id")
    private String businessId;

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