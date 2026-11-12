package cn.edu.sdua.sushe.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 站内消息 / 在线沟通。
 */
@Data
@Entity
@Table(name = "sys_message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fromUserId;

    private String fromUserName;

    private Long toUserId;

    private String toUserName;

    @Column(length = 1000)
    private String content;

    /** SYSTEM(系统通知)/CHAT(在线沟通)/ORDER(工单相关) */
    private String type;

    /** 关联工单 */
    private Long orderId;

    private Boolean readFlag;

    private LocalDateTime createTime;

    @PrePersist
    public void prePersist() {
        if (createTime == null) createTime = LocalDateTime.now();
        if (readFlag == null) readFlag = false;
        if (type == null) type = "SYSTEM";
    }
}
