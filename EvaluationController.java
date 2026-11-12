package cn.edu.sdua.sushe.controller;

import cn.edu.sdua.sushe.common.R;
import cn.edu.sdua.sushe.common.UserContext;
import cn.edu.sdua.sushe.entity.Evaluation;
import cn.edu.sdua.sushe.entity.RepairOrder;
import cn.edu.sdua.sushe.repository.EvaluationRepository;
import cn.edu.sdua.sushe.repository.RepairOrderRepository;
import cn.edu.sdua.sushe.repository.UserRepository;
import cn.edu.sdua.sushe.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 服务评价：学生对已完成工单评分，联动维修工绩效。
 */
@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

    @Autowired
    private EvaluationRepository repo;
    @Autowired
    private RepairOrderRepository orderRepo;
    @Autowired
    private UserRepository userRepo;

    /** 学生提交评价 */
    @PostMapping
    public R<Evaluation> submit(@RequestBody Evaluation ev) {
        ev.setStudentId(UserContext.userId());
        ev.setStudentName(UserContext.name());
        RepairOrder o = orderRepo.findById(ev.getOrderId())
                .orElseThrow(() -> new RuntimeException("工单不存在"));
        ev.setWorkerId(o.getWorkerId());
        ev.setWorkerName(o.getWorkerName());
        if (repo.findByOrderId(ev.getOrderId()) != null) {
            throw new RuntimeException("该工单已评价");
        }
        Evaluation saved = repo.save(ev);
        refreshWorkerRating(o.getWorkerId());
        return R.ok("评价成功，感谢您的反馈", saved);
    }

    @GetMapping("/my")
    public R<List<Evaluation>> my() {
        return R.ok(repo.findByStudentIdOrderByCreateTimeDesc(UserContext.userId()));
    }

    /** 维修工查看收到的评价（服务评价/绩效数据） */
    @GetMapping("/worker/{workerId}")
    public R<List<Evaluation>> workerEvals(@PathVariable Long workerId) {
        return R.ok(repo.findByWorkerIdOrderByCreateTimeDesc(workerId));
    }

    private void refreshWorkerRating(Long workerId) {
        if (workerId == null) return;
        List<Evaluation> list = repo.findByWorkerIdOrderByCreateTimeDesc(workerId);
        if (list.isEmpty()) return;
        double avg = list.stream().mapToInt(Evaluation::getScore).average().orElse(0);
        User w = userRepo.findById(workerId).orElse(null);
        if (w != null) {
            w.setAvgRating(Math.round(avg * 10) / 10.0);
            userRepo.save(w);
        }
    }
}
