package vip.lsjscl.flowboot.flow.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "dk_workflow_version")
public class WorkflowVersion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long workflowId;
    
    @Column(nullable = false)
    private Integer version;
    
    @Column(columnDefinition = "text")
    private String flowData;
    
    private LocalDateTime createTime;
    
    @PrePersist
    public void prePersist() {
        createTime = LocalDateTime.now();
    }
} 