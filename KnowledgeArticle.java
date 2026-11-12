package cn.edu.sdua.sushe.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 智能知识库文章（学生可自助查询解决方案）。
 */
@Data
@Entity
@Table(name = "knowledge_article")
public class KnowledgeArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    /** 分类：水电/网络/家电/家具 等 */
    private String category;

    @Column(length = 4000)
    private String content;

    /** 关键词，空格分隔，用于智能匹配 */
    private String keywords;

    /** 浏览量 */
    private Integer views;

    @PrePersist
    public void prePersist() {
        if (views == null) views = 0;
    }
}
