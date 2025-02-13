package vip.lsjscl.flowboot.flow.entity;


import jakarta.persistence.*;

/**
 * 人事审核表
 *
 * @author zhangxingju
 * @date 2025/02/13
 */
@Entity
@Table(name = "dk_personnel_audit")
public class PersonnelAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 关联到对应的活动
    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;
    
    // 审核人
    @Column(name = "approver")
    private String approver;

    @Column(name = "status")
    private String status; // pending、approved、rejected 等
    
    // getters and setters ...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Activity getActivity() { return activity; }
    public void setActivity(Activity activity) { this.activity = activity; }
    public String getApprover() { return approver; }
    public void setApprover(String approver) { this.approver = approver; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
} 