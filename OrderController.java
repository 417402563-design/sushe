package cn.edu.sdua.sushe.controller;

import cn.edu.sdua.sushe.common.R;
import cn.edu.sdua.sushe.common.UserContext;
import cn.edu.sdua.sushe.entity.Message;
import cn.edu.sdua.sushe.entity.RepairOrder;
import cn.edu.sdua.sushe.entity.User;
import cn.edu.sdua.sushe.repository.MessageRepository;
import cn.edu.sdua.sushe.repository.RepairOrderRepository;
import cn.edu.sdua.sushe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 报修工单：学生报修、维修工处理、管理员派单/审核 全流程。
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private RepairOrderRepository orderRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private MessageRepository messageRepo;

    /** 学生一键报修 */
    @PostMapping
    public R<RepairOrder> create(@RequestBody RepairOrder order) {
        order.setStudentId(UserContext.userId());
        order.setStudentName(UserContext.name());
        order.setStatus("PENDING");
        order.setWorkerId(null);
        order.setWorkerName(null);
        return R.ok("报修提交成功，等待派单", orderRepo.save(order));
    }

    /** 学生我的报修历史 */
    @GetMapping("/my")
    public R<List<RepairOrder>> my() {
        return R.ok(orderRepo.findByStudentIdOrderByCreateTimeDesc(UserContext.userId()));
    }

    /** 维修工：待抢单池（未派单） */
    @GetMapping("/pool")
    public R<List<RepairOrder>> pool() {
        return R.ok(orderRepo.findByStatusOrderByCreateTimeDesc("PENDING"));
    }

    /** 维修工：我的工单 */
    @GetMapping("/worker")
    public R<List<RepairOrder>> workerOrders() {
        return R.ok(orderRepo.findByWorkerIdOrderByCreateTimeDesc(UserContext.userId()));
    }

    /** 管理员：全部工单，可按状态过滤 */
    @GetMapping
    public R<List<RepairOrder>> list(@RequestParam(required = false) String status) {
        if (status != null && !status.isBlank()) {
            return R.ok(orderRepo.findByStatusOrderByCreateTimeDesc(status));
        }
        return R.ok(orderRepo.findAll());
    }

    @GetMapping("/{id}")
    public R<RepairOrder> detail(@PathVariable Long id) {
        return R.ok(orderRepo.findById(id).orElse(null));
    }

    /** 管理员派单 */
    @PutMapping("/{id}/assign")
    public R<RepairOrder> assign(@PathVariable Long id, @RequestParam Long workerId) {
        RepairOrder o = orderRepo.findById(id).orElseThrow(() -> new RuntimeException("工单不存在"));
        User w = userRepo.findById(workerId).orElseThrow(() -> new RuntimeException("维修工不存在"));
        o.setWorkerId(workerId);
        o.setWorkerName(w.getRealName());
        o.setStatus("ASSIGNED");
        RepairOrder saved = orderRepo.save(o);
        notify(w.getId(), UserContext.userId(), "ORDER",
                "您有新的工单【" + o.getTitle() + "】待处理");
        notify(o.getStudentId(), UserContext.userId(), "ORDER",
                "您的报修【" + o.getTitle() + "】已派单给维修工 " + w.getRealName());
        return R.ok(saved);
    }

    /** 维修工抢单（自助领取待派单工单） */
    @PutMapping("/{id}/grab")
    public R<RepairOrder> grab(@PathVariable Long id) {
        RepairOrder o = orderRepo.findById(id).orElseThrow(() -> new RuntimeException("工单不存在"));
        if (!"PENDING".equals(o.getStatus())) {
            throw new RuntimeException("该工单已被处理");
        }
        o.setWorkerId(UserContext.userId());
        o.setWorkerName(UserContext.name());
        o.setStatus("PROCESSING");
        o.setAcceptTime(LocalDateTime.now());
        return R.ok("接单成功", orderRepo.save(o));
    }

    /** 维修工开始处理 */
    @PutMapping("/{id}/accept")
    public R<RepairOrder> accept(@PathVariable Long id) {
        RepairOrder o = orderRepo.findById(id).orElseThrow(() -> new RuntimeException("工单不存在"));
        o.setStatus("PROCESSING");
        o.setAcceptTime(LocalDateTime.now());
        return R.ok(orderRepo.save(o));
    }

    /** 维修工完成维修，提交审核 */
    @PutMapping("/{id}/finish")
    public R<RepairOrder> finish(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        RepairOrder o = orderRepo.findById(id).orElseThrow(() -> new RuntimeException("工单不存在"));
        o.setFinishRemark((String) body.get("finishRemark"));
        Object wh = body.get("workHours");
        if (wh != null) o.setWorkHours(Double.valueOf(wh.toString()));
        o.setFinishTime(LocalDateTime.now());
        o.setStatus("DONE");
        RepairOrder saved = orderRepo.save(o);
        notify(o.getStudentId(), UserContext.userId(), "ORDER",
                "您的报修【" + o.getTitle() + "】维修已完成，请及时评价");
        return R.ok("已提交，待管理员/用户确认", saved);
    }

    /** 管理员审核：通过->CONFIRMED，不通过->REWORK */
    @PutMapping("/{id}/audit")
    public R<RepairOrder> audit(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        RepairOrder o = orderRepo.findById(id).orElseThrow(() -> new RuntimeException("工单不存在"));
        boolean approved = Boolean.parseBoolean(String.valueOf(body.get("approved")));
        o.setAuditRemark((String) body.get("remark"));
        if (approved) {
            o.setStatus("CONFIRMED");
            if (o.getWorkerId() != null) {
                User w = userRepo.findById(o.getWorkerId()).orElse(null);
                if (w != null) {
                    w.setFinishedCount((w.getFinishedCount() == null ? 0 : w.getFinishedCount()) + 1);
                    w.setTotalWorkHours((w.getTotalWorkHours() == null ? 0 : w.getTotalWorkHours())
                            + (o.getWorkHours() == null ? 0 : o.getWorkHours()));
                    userRepo.save(w);
                }
            }
        } else {
            o.setStatus("REWORK");
        }
        RepairOrder saved = orderRepo.save(o);
        notify(o.getStudentId(), UserContext.userId(), "ORDER",
                "您的报修【" + o.getTitle() + "】审核" + (approved ? "已通过" : "需返修"));
        return R.ok(saved);
    }

    /** 学生取消自己的工单（仅待派单状态） */
    @PutMapping("/{id}/cancel")
    public R<RepairOrder> cancel(@PathVariable Long id) {
        RepairOrder o = orderRepo.findById(id).orElseThrow(() -> new RuntimeException("工单不存在"));
        if (!"PENDING".equals(o.getStatus())) {
            throw new RuntimeException("当前状态不可取消");
        }
        o.setStatus("CANCELED");
        return R.ok(orderRepo.save(o));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        orderRepo.deleteById(id);
        return R.ok();
    }

    private void notify(Long toUserId, Long fromUserId, String type, String content) {
        if (toUserId == null) return;
        Message m = new Message();
        m.setToUserId(toUserId);
        m.setFromUserId(fromUserId);
        m.setType(type);
        m.setContent(content);
        messageRepo.save(m);
    }
}
