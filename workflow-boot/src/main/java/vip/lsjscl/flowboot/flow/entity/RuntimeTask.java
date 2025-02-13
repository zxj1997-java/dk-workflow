package vip.lsjscl.flowboot.flow.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

/**
 * 运行时任务
 * @author zhangxingju
 * @date 2025/02/13
 */
@Data
@Entity
@Table(name = "dk_runtime_task")
public class RuntimeTask {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("关联到相应的活动")
    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @Comment("任务状态，如 pending、completed 等")
    @Column(name = "status")
    private String status;
} 