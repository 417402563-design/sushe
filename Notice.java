package cn.edu.sdua.sushe.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 通知公告。
 */
@Data
@Entity
@Table(name = "notice")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String content;

    private String author;

    /** 是否置顶 */
    @Column(name = "is_top")
    private Boolean top;

    private LocalDateTime publishTime;

    @PrePersist
    public void prePersist() {
        if (publishTime == null) publishTime = LocalDateTime.now();
        if (top == null) top = false;
    }
}
