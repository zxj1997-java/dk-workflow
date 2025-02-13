package vip.lsjscl.flowboot.flow.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

/**
 * 工作流审批
 *
 * @author zhangxingju
 * @date 2025/02/13
 */
@Data
@Entity
@Table(name = "dk_workflow_approval")
public class WorkflowApproval {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("工作流实例ID")
    @Column(nullable = false)
    private Long instanceId;

    @Comment("节点ID")
    @Column(nullable = false)
    private String nodeId;

    @Comment("节点名称")
    private String nodeName;

    @Comment("审批人")
    private String approver;

    @Comment("审批动作")
    private String action;

    @Comment("审批意见")
    private String comment;
    
    private LocalDateTime createTime;
    
    @PrePersist
    public void prePersist() {
        createTime = LocalDateTime.now();
    }
} 