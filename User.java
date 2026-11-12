package cn.edu.sdua.sushe.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 统一用户表：管理员 / 维修工 / 学生 三种角色。
 */
@Data
@Entity
@Table(name = "sys_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    private String realName;

    /** ADMIN / WORKER / STUDENT */
    private String role;

    private String phone;

    private String email;

    /** 性别：男/女 */
    private String gender;

    /** 维修工技能，逗号分隔，如：水电,木工,家电 */
    private String skills;

    /** 维修工平均评分 */
    private Double avgRating;

    /** 维修工累计完成工单数 */
    private Integer finishedCount;

    /** 维修工累计工时（小时） */
    private Double totalWorkHours;

    /** 学生所在学院/班级 */
    private String college;

    /** 状态：0-禁用 1-正常 */
    private Integer status;

    private String avatar;

    private LocalDateTime createTime;

    @PrePersist
    public void prePersist() {
        if (createTime == null) createTime = LocalDateTime.now();
        if (status == null) status = 1;
        if (avgRating == null) avgRating = 0.0;
        if (finishedCount == null) finishedCount = 0;
        if (totalWorkHours == null) totalWorkHours = 0.0;
    }
}
