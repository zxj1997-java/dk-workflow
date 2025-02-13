package vip.lsjscl.flowboot.flow.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

/**
 * 变迁
 *
 * @author zhangxingju
 * @date 2025/02/13
 */
@Data
@Entity
@Table(name = "dk_transition")
public class Transition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_activity_id")
    private Activity fromActivity;

    @ManyToOne
    @JoinColumn(name = "to_activity_id")
    private Activity toActivity;

    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "condition_class")
    private String conditionClass;

    @Column(name = "after_class")
    private String afterClass;

    // 新增字段：流程版本ID，用于标识该变迁记录所属的流程版本
    @Comment("流程版本ID")
    @Column(name = "workflow_version_id")
    private Long workflowVersionId;
} 