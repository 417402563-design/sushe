package cn.edu.sdua.sushe.controller;

import cn.edu.sdua.sushe.common.R;
import cn.edu.sdua.sushe.common.UserContext;
import cn.edu.sdua.sushe.entity.Schedule;
import cn.edu.sdua.sushe.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 维修工工时排班。
 */
@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleRepository repo;

    /** 我的排班 */
    @GetMapping("/my")
    public R<List<Schedule>> my() {
        return R.ok(repo.findByWorkerIdOrderByWorkDateAsc(UserContext.userId()));
    }

    /** 指定维修工排班（管理员查看） */
    @GetMapping("/worker/{workerId}")
    public R<List<Schedule>> byWorker(@PathVariable Long workerId) {
        return R.ok(repo.findByWorkerIdOrderByWorkDateAsc(workerId));
    }

    @GetMapping
    public R<List<Schedule>> list() {
        return R.ok(repo.findAll());
    }

    @PostMapping
    public R<Schedule> add(@RequestBody Schedule s) {
        s.setId(null);
        if (s.getWorkerName() == null) {
            s.setWorkerName(UserContext.name());
        }
        return R.ok("排班成功", repo.save(s));
    }

    @PutMapping("/{id}")
    public R<Schedule> update(@PathVariable Long id, @RequestBody Schedule s) {
        s.setId(id);
        return R.ok("更新成功", repo.save(s));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        repo.deleteById(id);
        return R.ok();
    }
}
