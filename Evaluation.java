package cn.edu.sdua.sushe.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学生对维修服务的评价。
 */
@Data
@Entity
@Table(name = "evaluation")
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private Long studentId;

    private String studentName;

    private Long workerId;

    private String workerName;

    /** 综合评分 1-5 */
    private Integer score;

    /** 响应速度 1-5 */
    private Integer speedScore;

    /** 维修质量 1-5 */
    private Integer qualityScore;

    /** 服务态度 1-5 */
    private Integer attitudeScore;

    @Column(length = 1000)
    private String comment;

    private LocalDateTime createTime;

    @PrePersist
    public void prePersist() {
        if (createTime == null) createTime = LocalDateTime.now();
    }
}
