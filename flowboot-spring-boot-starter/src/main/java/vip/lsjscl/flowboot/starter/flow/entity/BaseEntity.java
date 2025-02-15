package vip.lsjscl.flowboot.starter.flow.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

/**
 * 基础实体类
 * 提供所有实体通用的字段
 *
 * @author zhangxingju
 * @date 2025/02/15
 */
@Data
@MappedSuperclass
@SQLDelete(sql = "UPDATE #{#entityName} SET is_deleted = true WHERE id = ? AND is_deleted = false")
@Where(clause = "is_deleted = false")
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Comment("创建时间")
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @Comment("更新时间")
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Comment("是否删除")
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Comment("创建人")
    @Column(name = "create_by")
    private String createBy;

    @Comment("更新人")
    @Column(name = "update_by")
    private String updateBy;

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        this.updateTime = this.createTime;
        this.isDeleted = false;
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
} 