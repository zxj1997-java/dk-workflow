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
    
    @Column(nullable = false, unique = true)
    private String code;
    
    @Column(name = "flow_data", columnDefinition = "text")
    private String flowData;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(nullable = false)
    private String status;
    
    @Transient
    private Integer currentVersion;
    
    @Transient
    @OneToMany(mappedBy = "workflowId")
    private List<WorkflowVersion> versions;
    
    @PrePersist
    public void prePersist() {
        createTime = LocalDateTime.now();
    }
} 