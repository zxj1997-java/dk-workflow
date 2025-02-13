package vip.lsjscl.flowboot.flow.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

/**
 * 工作流实例
 *
 * @author zhangxingju
 * @date 2025/02/13
 */
@Data
@Entity
@Table(name = "dk_workflow_instance")
public class WorkflowInstance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Comment("工作流ID")
    @Column(name = "workflow_id")
    private Long workflowId;
    
    @Comment("业务关联键")
    @Column(name = "business_key")
    private String businessKey;
    
    @Comment("当前节点")
    @Column(name = "current_node")
    private String currentNode;
    
    @Comment("实例状态")
    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    @Comment("版本号，不能为空")
    private Integer version;
    
    @Comment("创建时间")
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Comment("更新时间")
    @Column(name = "update_time")
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