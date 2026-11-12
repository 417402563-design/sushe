package cn.edu.sdua.sushe.controller;

import cn.edu.sdua.sushe.common.R;
import cn.edu.sdua.sushe.entity.KnowledgeArticle;
import cn.edu.sdua.sushe.repository.KnowledgeArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 智能知识库：关键词智能匹配 + 分类浏览 + 管理端维护。
 */
@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    @Autowired
    private KnowledgeArticleRepository repo;

    /** 智能查询：输入问题关键词，返回匹配方案 */
    @GetMapping("/search")
    public R<List<KnowledgeArticle>> search(@RequestParam String kw) {
        return R.ok(repo.search(kw));
    }

    @GetMapping("/category/{category}")
    public R<List<KnowledgeArticle>> byCategory(@PathVariable String category) {
        return R.ok(repo.findByCategory(category));
    }

    @GetMapping
    public R<List<KnowledgeArticle>> list() {
        return R.ok(repo.findAll());
    }

    /** 查看详情并累加浏览量 */
    @GetMapping("/{id}")
    public R<KnowledgeArticle> detail(@PathVariable Long id) {
        KnowledgeArticle a = repo.findById(id).orElse(null);
        if (a != null) {
            a.setViews((a.getViews() == null ? 0 : a.getViews()) + 1);
            repo.save(a);
        }
        return R.ok(a);
    }

    @PostMapping
    public R<KnowledgeArticle> add(@RequestBody KnowledgeArticle a) {
        a.setId(null);
        return R.ok("发布成功", repo.save(a));
    }

    @PutMapping("/{id}")
    public R<KnowledgeArticle> update(@PathVariable Long id, @RequestBody KnowledgeArticle a) {
        a.setId(id);
        return R.ok("更新成功", repo.save(a));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        repo.deleteById(id);
        return R.ok();
    }
}
