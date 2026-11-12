package cn.edu.sdua.sushe.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 报修工单。
 *
 * status 流程：
 *   PENDING(待派单) -> ASSIGNED(已派单) -> PROCESSING(维修中)
 *   -> DONE(已完成,待审核) -> CONFIRMED(已确认/已闭环) 或 REWORK(返修)
 *   审核不通过可 CANCELED(已取消)。
 */
@Data
@Entity
@Table(name = "repair_order")
public class RepairOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 报修学生ID */
    private Long studentId;

    private String studentName;

    /** 宿舍房间ID */
    private Long roomId;

    private String building;

    private String roomNo;

    /** 报修类别：水电/家具/门窗/网络/家电/其他 */
    private String category;

    private String title;

    @Column(length = 1000)
    private String description;

    /** 紧急报修图片地址 */
    private String imagePath;

    /** 图片自动识别结果（识别故障类型/严重程度） */
    private String imageRecognize;

    /** 紧急程度：普通/紧急 */
    private String urgency;

    /** 是否自助解决尝试 */
    private String selfSolved;

    /** 工单状态 */
    private String status;

    /** 派单维修工ID */
    private Long workerId;

    private String workerName;

    /** 管理员审核意见 */
    private String auditRemark;

    /** 维修结果说明 */
    private String finishRemark;

    /** 耗费工时（小时） */
    private Double workHours;

    private LocalDateTime createTime;

    private LocalDateTime acceptTime;

    private LocalDateTime finishTime;

    @PrePersist
    public void prePersist() {
        if (createTime == null) createTime = LocalDateTime.now();
        if (status == null) status = "PENDING";
        if (urgency == null) urgency = "普通";
    }
}
