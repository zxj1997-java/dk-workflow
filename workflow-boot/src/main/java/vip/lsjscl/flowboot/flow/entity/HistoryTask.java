package vip.lsjscl.flowboot.flow.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;
import vip.lsjscl.flowboot.flow.dict.TaskStatus;

import java.time.LocalDateTime;

/**
 * 历史任务
 *
 * @author zhangxingju
 * @date 2025/02/13
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

    // 关联到活动
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

    @Column(name = "comment")
    @Comment("审批意见")
    private String comment;
} 