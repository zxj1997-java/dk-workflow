package vip.lsjscl.flowboot.flow.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "dk_workflow_instance")
public class WorkflowInstance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long workflowId;
    
    @Column(nullable = false)
    private Integer version;
    
    private String businessKey;  // 业务关联键
    
    private String currentNode;  // 当前节点ID
    
    @Column(columnDefinition = "text")
    private String flowData;  // 流程数据
    
    private String status;  // 状态：进行中、已完成、已终止
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    @PrePersist
    public void prePersist() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }
} 