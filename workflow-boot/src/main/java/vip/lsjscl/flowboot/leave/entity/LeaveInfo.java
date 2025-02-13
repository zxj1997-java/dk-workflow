package vip.lsjscl.flowboot.leave.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

@Data
@Entity
@Table(name = "leave_info")
public class LeaveInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("姓名")
    @Column(name = "name")
    private String name;

    @Comment("原因")
    @Column(name = "reason")
    private String reason;


    @Comment("离职日期")
    @Column(name = "leave_date")
    private String leaveDate;


    @Comment("状态")
    @Column(name = "status")
    private String status;

}

