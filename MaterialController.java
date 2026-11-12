package cn.edu.sdua.sushe.controller;

import cn.edu.sdua.sushe.common.R;
import cn.edu.sdua.sushe.common.UserContext;
import cn.edu.sdua.sushe.entity.MaterialRequest;
import cn.edu.sdua.sushe.entity.SparePart;
import cn.edu.sdua.sushe.repository.MaterialRequestRepository;
import cn.edu.sdua.sushe.repository.SparePartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 维修工材料申请 + 管理员审批。
 */
@RestController
@RequestMapping("/api/materials")
public class MaterialController {

    @Autowired
    private MaterialRequestRepository repo;
    @Autowired
    private SparePartRepository partRepo;

    /** 维修工提交材料申请 */
    @PostMapping
    public R<MaterialRequest> apply(@RequestBody MaterialRequest req) {
        req.setWorkerId(UserContext.userId());
        req.setWorkerName(UserContext.name());
        req.setStatus("PENDING");
        SparePart p = partRepo.findById(req.getPartId()).orElse(null);
        if (p != null) req.setPartName(p.getPartName());
        return R.ok("申请已提交", repo.save(req));
    }

    /** 维修工查看我的申请 */
    @GetMapping("/my")
    public R<List<MaterialRequest>> my() {
        return R.ok(repo.findByWorkerIdOrderByCreateTimeDesc(UserContext.userId()));
    }

    /** 管理员查看全部/按状态 */
    @GetMapping
    public R<List<MaterialRequest>> list(@RequestParam(required = false) String status) {
        if (status != null && !status.isBlank()) {
            return R.ok(repo.findByStatusOrderByCreateTimeDesc(status));
        }
        return R.ok(repo.findAll());
    }

    /** 管理员审批：通过则扣减库存 */
    @PutMapping("/{id}/audit")
    public R<MaterialRequest> audit(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        MaterialRequest req = repo.findById(id).orElseThrow(() -> new RuntimeException("申请不存在"));
        boolean approved = Boolean.parseBoolean(String.valueOf(body.get("approved")));
        req.setAuditRemark((String) body.get("remark"));
        if (approved) {
            req.setStatus("APPROVED");
            SparePart p = partRepo.findById(req.getPartId()).orElse(null);
            if (p != null) {
                int left = (p.getStock() == null ? 0 : p.getStock()) - (req.getQuantity() == null ? 0 : req.getQuantity());
                if (left < 0) throw new RuntimeException("库存不足，无法审批");
                p.setStock(left);
                partRepo.save(p);
            }
        } else {
            req.setStatus("REJECTED");
        }
        return R.ok(repo.save(req));
    }
}
