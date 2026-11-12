package cn.edu.sdua.sushe.controller;

import cn.edu.sdua.sushe.common.R;
import cn.edu.sdua.sushe.entity.SparePart;
import cn.edu.sdua.sushe.repository.SparePartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 备件库存管理。
 */
@RestController
@RequestMapping("/api/parts")
public class SparePartController {

    @Autowired
    private SparePartRepository repo;

    @GetMapping
    public R<List<SparePart>> list() {
        return R.ok(repo.findAll());
    }

    /** 库存预警：低于安全库存 */
    @GetMapping("/low")
    public R<List<SparePart>> low() {
        List<SparePart> all = repo.findAll();
        all.removeIf(p -> p.getStock() == null || p.getSafetyStock() == null
                || p.getStock() > p.getSafetyStock());
        return R.ok(all);
    }

    @PostMapping
    public R<SparePart> add(@RequestBody SparePart part) {
        part.setId(null);
        return R.ok("保存成功", repo.save(part));
    }

    @PutMapping("/{id}")
    public R<SparePart> update(@PathVariable Long id, @RequestBody SparePart part) {
        part.setId(id);
        return R.ok("更新成功", repo.save(part));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        repo.deleteById(id);
        return R.ok();
    }
}
