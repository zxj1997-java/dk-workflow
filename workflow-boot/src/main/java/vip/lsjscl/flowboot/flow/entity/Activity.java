package vip.lsjscl.flowboot.flow.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

/**
 * 活动
 *
 * @author zhangxingju
 * @date 2025/02/13
 */
@Data
@Entity
@Table(name = "dk_activity")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 用于关联前端 JSON 中的节点 ID
    @Column(name = "node_id", unique = true)
    private String nodeId;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "code")
    private String code;
    
    @Column(name = "page_url")
    private String pageUrl;
    
    @Column(name = "after_class")
    private String afterClass;
    
    // 为简单起见，将 List 类型转换为逗号分隔字符串存储（实际业务中可采用 join table 方式）
    @Column(name = "approvers")
    private String approvers;
    
    @Column(name = "departments")
    private String departments;
    
    @Column(name = "operations")
    private String operations;

    @Comment("流程版本ID")
    @Column(name = "workflow_version_id")
    private Long workflowVersionId;
} 