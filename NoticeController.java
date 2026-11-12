package cn.edu.sdua.sushe.controller;

import cn.edu.sdua.sushe.common.R;
import cn.edu.sdua.sushe.common.UserContext;
import cn.edu.sdua.sushe.entity.Notice;
import cn.edu.sdua.sushe.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知公告。
 */
@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    @Autowired
    private NoticeRepository repo;

    @GetMapping
    public R<List<Notice>> list() {
        return R.ok(repo.findAllByOrderByTopDescPublishTimeDesc());
    }

    @GetMapping("/{id}")
    public R<Notice> detail(@PathVariable Long id) {
        return R.ok(repo.findById(id).orElse(null));
    }

    @PostMapping
    public R<Notice> add(@RequestBody Notice notice) {
        notice.setId(null);
        if (notice.getAuthor() == null) notice.setAuthor(UserContext.name());
        return R.ok("发布成功", repo.save(notice));
    }

    @PutMapping("/{id}")
    public R<Notice> update(@PathVariable Long id, @RequestBody Notice notice) {
        notice.setId(id);
        return R.ok("更新成功", repo.save(notice));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        repo.deleteById(id);
        return R.ok();
    }
}
