package vip.lsjscl.flowboot.flow.entity;

import jakarta.persistence.*;
import lombok.Data;

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
} 