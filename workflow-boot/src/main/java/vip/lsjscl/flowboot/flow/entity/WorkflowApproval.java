package vip.lsjscl.flowboot.flow.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "dk_workflow_approval")
public class WorkflowApproval {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long instanceId;  // 工作流实例ID
    
    @Column(nullable = false)
    private String nodeId;  // 节点ID
    
    private String nodeName;  // 节点名称
    
    private String approver;  // 审批人
    
    private String action;  // 审批动作
    
    private String comment;  // 审批意见
    
    private LocalDateTime createTime;
    
    @PrePersist
    public void prePersist() {
        createTime = LocalDateTime.now();
    }
} 