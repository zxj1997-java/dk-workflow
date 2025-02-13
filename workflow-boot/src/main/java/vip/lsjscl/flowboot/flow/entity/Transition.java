package vip.lsjscl.flowboot.flow.entity;

import jakarta.persistence.*;

/**
 * 变迁
 *
 * @author zhangxingju
 * @date 2025/02/13
 */
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

    // getters and setters ...
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Activity getFromActivity() {
        return fromActivity;
    }

    public void setFromActivity(Activity fromActivity) {
        this.fromActivity = fromActivity;
    }

    public Activity getToActivity() {
        return toActivity;
    }

    public void setToActivity(Activity toActivity) {
        this.toActivity = toActivity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getConditionClass() {
        return conditionClass;
    }

    public void setConditionClass(String conditionClass) {
        this.conditionClass = conditionClass;
    }

    public String getAfterClass() {
        return afterClass;
    }

    public void setAfterClass(String afterClass) {
        this.afterClass = afterClass;
    }
} 