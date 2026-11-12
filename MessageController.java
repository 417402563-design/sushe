package cn.edu.sdua.sushe.controller;

import cn.edu.sdua.sushe.common.R;
import cn.edu.sdua.sushe.common.UserContext;
import cn.edu.sdua.sushe.entity.Message;
import cn.edu.sdua.sushe.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 消息通知 + 在线沟通。
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageRepository repo;

    /** 我的收件箱（消息通知） */
    @GetMapping("/inbox")
    public R<List<Message>> inbox() {
        return R.ok(repo.findByToUserIdOrderByCreateTimeAsc(UserContext.userId()));
    }

    /** 未读数量 */
    @GetMapping("/unread")
    public R<Long> unread() {
        return R.ok(repo.countByToUserIdAndReadFlagFalse(UserContext.userId()));
    }

    /** 两人在线沟通会话 */
    @GetMapping("/conversation/{peerId}")
    public R<List<Message>> conversation(@PathVariable Long peerId) {
        return R.ok(repo.findConversation(UserContext.userId(), peerId));
    }

    /** 发送消息 */
    @PostMapping("/send")
    public R<Message> send(@RequestBody Map<String, Object> body) {
        Message m = new Message();
        m.setFromUserId(UserContext.userId());
        m.setFromUserName(UserContext.name());
        m.setToUserId(Long.valueOf(String.valueOf(body.get("toUserId"))));
        m.setContent((String) body.get("content"));
        m.setType(body.get("type") == null ? "CHAT" : String.valueOf(body.get("type")));
        if (body.get("orderId") != null) {
            m.setOrderId(Long.valueOf(String.valueOf(body.get("orderId"))));
        }
        return R.ok(repo.save(m));
    }

    /** 标记已读 */
    @PutMapping("/{id}/read")
    public R<Void> read(@PathVariable Long id) {
        repo.findById(id).ifPresent(m -> {
            m.setReadFlag(true);
            repo.save(m);
        });
        return R.ok();
    }

    @PutMapping("/read-all")
    public R<Void> readAll() {
        List<Message> inbox = repo.findByToUserIdOrderByCreateTimeAsc(UserContext.userId());
        inbox.forEach(m -> {
            if (!Boolean.TRUE.equals(m.getReadFlag())) {
                m.setReadFlag(true);
                repo.save(m);
            }
        });
        return R.ok();
    }
}
