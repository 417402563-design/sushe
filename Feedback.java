package cn.edu.sdua.sushe.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 意见反馈 / 投诉。
 */
@Data
@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;

    private String studentName;

    /** OPINION(意见反馈) / COMPLAINT(投诉) */
    private String type;

    @Column(length = 2000)
    private String content;

    /** 关联工单（可选） */
    private Long orderId;

    /** PENDING/REPLIED */
    private String status;

    @Column(length = 2000)
    private String reply;

    private LocalDateTime createTime;

    private LocalDateTime replyTime;

    @PrePersist
    public void prePersist() {
        if (createTime == null) createTime = LocalDateTime.now();
        if (status == null) status = "PENDING";
        if (type == null) type = "OPINION";
    }
}
