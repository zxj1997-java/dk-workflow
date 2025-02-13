package vip.lsjscl.flowboot.flow.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

/**
 * 人事审核表
 *
 * @author zhangxingju
 * @date 2025/02/13
 */
@Data
@Entity
@Table(name = "dk_personnel_audit")
public class PersonnelAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("关联到对应的活动")
    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;
    
    @Comment("审核人")
    @Column(name = "approver")
    private String approver;

    @Comment("状态pending、approved、rejected 等")
    @Column(name = "status")
    private String status;
} 