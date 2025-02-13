package vip.lsjscl.flowboot.flow.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 历史任务
 *
 * @author zhangxingju
 * @date 2025/02/13
 */
@Data
@Entity
@Table(name = "dk_history_task")
public class HistoryTask {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 关联到活动
    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @Column(name = "status")
    private String status;
} 