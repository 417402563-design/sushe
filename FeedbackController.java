package cn.edu.sdua.sushe.controller;

import cn.edu.sdua.sushe.common.R;
import cn.edu.sdua.sushe.common.UserContext;
import cn.edu.sdua.sushe.entity.Feedback;
import cn.edu.sdua.sushe.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 意见反馈 / 投诉：学生提交，管理员回复处理。
 */
@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackRepository repo;

    @PostMapping
    public R<Feedback> submit(@RequestBody Feedback fb) {
        fb.setStudentId(UserContext.userId());
        fb.setStudentName(UserContext.name());
        fb.setStatus("PENDING");
        return R.ok("提交成功，感谢您的反馈", repo.save(fb));
    }

    @GetMapping("/my")
    public R<List<Feedback>> my() {
        return R.ok(repo.findByStudentIdOrderByCreateTimeDesc(UserContext.userId()));
    }

    @GetMapping
    public R<List<Feedback>> list(@RequestParam(required = false) String status) {
        if (status != null && !status.isBlank()) {
            return R.ok(repo.findByStatusOrderByCreateTimeDesc(status));
        }
        return R.ok(repo.findAll());
    }

    /** 管理员回复 */
    @PutMapping("/{id}/reply")
    public R<Feedback> reply(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Feedback fb = repo.findById(id).orElseThrow(() -> new RuntimeException("反馈不存在"));
        fb.setReply(body.get("reply"));
        fb.setStatus("REPLIED");
        fb.setReplyTime(LocalDateTime.now());
        return R.ok("已回复", repo.save(fb));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        repo.deleteById(id);
        return R.ok();
    }
}
