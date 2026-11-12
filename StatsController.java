package cn.edu.sdua.sushe.controller;

import cn.edu.sdua.sushe.common.R;
import cn.edu.sdua.sushe.entity.RepairOrder;
import cn.edu.sdua.sushe.entity.SparePart;
import cn.edu.sdua.sushe.entity.User;
import cn.edu.sdua.sushe.repository.RepairOrderRepository;
import cn.edu.sdua.sushe.repository.SparePartRepository;
import cn.edu.sdua.sushe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据统计（管理员仪表盘）。
 */
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private RepairOrderRepository orderRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private SparePartRepository partRepo;

    @GetMapping("/dashboard")
    public R<Map<String, Object>> dashboard() {
        Map<String, Object> data = new HashMap<>();

        List<RepairOrder> orders = orderRepo.findAll();

        // 工单状态分布
        Map<String, Long> statusDist = orders.stream()
                .collect(Collectors.groupingBy(RepairOrder::getStatus, Collectors.counting()));
        data.put("statusDist", statusDist);
        data.put("orderTotal", orders.size());

        // 报修类别分布
        Map<String, Long> categoryDist = orders.stream()
                .collect(Collectors.groupingBy(o -> o.getCategory() == null ? "其他" : o.getCategory(),
                        Collectors.counting()));
        data.put("categoryDist", categoryDist);

        // 用户统计
        data.put("workerCount", userRepo.countByRole("WORKER"));
        data.put("studentCount", userRepo.countByRole("STUDENT"));

        // 维修工绩效排行
        List<User> workers = userRepo.findByRole("WORKER");
        List<Map<String, Object>> ranking = new ArrayList<>();
        for (User w : workers) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", w.getRealName());
            item.put("finished", w.getFinishedCount());
            item.put("workHours", w.getTotalWorkHours());
            item.put("avgRating", w.getAvgRating());
            ranking.add(item);
        }
        ranking.sort((a, b) -> Double.compare(
                dbl(b.get("finished")), dbl(a.get("finished"))));
        data.put("workerRanking", ranking);

        // 备件库存预警数量
        List<SparePart> all = partRepo.findAll();
        long low = all.stream().filter(p -> p.getStock() != null && p.getSafetyStock() != null
                && p.getStock() <= p.getSafetyStock()).count();
        data.put("partTotal", all.size());
        data.put("partLowStock", low);

        return R.ok(data);
    }

    private double dbl(Object o) {
        return o == null ? 0 : Double.parseDouble(o.toString());
    }
}
