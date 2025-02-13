package vip.lsjscl.flowboot.flow.entity;


import jakarta.persistence.*;

/**
 * 运行时任务
 * @author zhangxingju
 * @date 2025/02/13
 */
@Entity
@Table(name = "dk_runtime_task")
public class RuntimeTask {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 关联到相应的活动
    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;

    // 任务状态，如 pending、completed 等
    @Column(name = "status")
    private String status;
    
    // getters and setters ...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Activity getActivity() { return activity; }
    public void setActivity(Activity activity) { this.activity = activity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
} 