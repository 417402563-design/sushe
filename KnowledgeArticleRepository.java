package cn.edu.sdua.sushe.repository;

import cn.edu.sdua.sushe.entity.KnowledgeArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KnowledgeArticleRepository extends JpaRepository<KnowledgeArticle, Long> {

    @Query("select k from KnowledgeArticle k where " +
            "k.title like %:kw% or k.content like %:kw% or k.keywords like %:kw% or k.category like %:kw%")
    List<KnowledgeArticle> search(@Param("kw") String kw);

    List<KnowledgeArticle> findByCategory(String category);
}
