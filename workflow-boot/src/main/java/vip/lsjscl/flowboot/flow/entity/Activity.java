package vip.lsjscl.flowboot.flow.entity;


import jakarta.persistence.*;

/**
 * 活动
 *
 * @author zhangxingju
 * @date 2025/02/13
 */
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

    // getters and setters ...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getPageUrl() { return pageUrl; }
    public void setPageUrl(String pageUrl) { this.pageUrl = pageUrl; }
    public String getAfterClass() { return afterClass; }
    public void setAfterClass(String afterClass) { this.afterClass = afterClass; }
    public String getApprovers() { return approvers; }
    public void setApprovers(String approvers) { this.approvers = approvers; }
    public String getDepartments() { return departments; }
    public void setDepartments(String departments) { this.departments = departments; }
    public String getOperations() { return operations; }
    public void setOperations(String operations) { this.operations = operations; }
} 