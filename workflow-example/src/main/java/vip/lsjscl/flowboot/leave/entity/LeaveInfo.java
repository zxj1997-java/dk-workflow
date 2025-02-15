package vip.lsjscl.flowboot.leave.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;
import vip.lsjscl.flowboot.starter.flow.entity.BaseEntity;

import java.util.Date;

/**
 * 请假信息
 *
 * @author zhangxingju
 * @date 2025/02/14
 */
@Data
@Entity
@Table(name = "leave_info")
public class LeaveInfo extends BaseEntity {

    @Comment("姓名")
    @Column(name = "name")
    private String name;

    @Comment("原因")
    @Column(name = "reason")
    private String reason;

    @Comment("开始日期")
    @Column(name = "start_date")
    private Date startDate;

    @Comment("结束日期")
    @Column(name = "end_date")
    private Date endDate;


    @Comment("状态")
    @Column(name = "status")
    private String status;

    @Comment("工作流版本")
    @Column(name = "workflow_version_id")
    private String workflowVersionId;

}

