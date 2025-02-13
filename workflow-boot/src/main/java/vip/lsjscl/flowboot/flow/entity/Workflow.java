package vip.lsjscl.flowboot.flow.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "dk_workflow")
public class Workflow {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "text")
    private String flowData;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    @Transient  // 不映射到数据库
    private Integer currentVersion;
    
    @Transient  // 不映射到数据库
    private List<WorkflowVersion> versions;
    
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