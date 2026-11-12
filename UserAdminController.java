package cn.edu.sdua.sushe.controller;

import cn.edu.sdua.sushe.common.R;
import cn.edu.sdua.sushe.entity.User;
import cn.edu.sdua.sushe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员：维修工 / 学生 用户管理。
 */
@RestController
@RequestMapping("/api/admin/users")
public class UserAdminController {

    @Autowired
    private UserRepository userRepo;

    /** 按角色列出，role 可为 WORKER / STUDENT / ADMIN，留空列全部 */
    @GetMapping
    public R<List<User>> list(@RequestParam(required = false) String role) {
        List<User> list = (role == null || role.isBlank())
                ? userRepo.findAll() : userRepo.findByRole(role);
        list.forEach(u -> u.setPassword(null));
        return R.ok(list);
    }

    /** 管理员新增用户（含新增维修工） */
    @PostMapping
    public R<User> add(@RequestBody User user) {
        if (userRepo.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }
        user.setId(null);
        if (user.getStatus() == null) user.setStatus(1);
        return R.ok("保存成功", userRepo.save(user));
    }

    @PutMapping("/{id}")
    public R<User> update(@PathVariable Long id, @RequestBody User user) {
        User db = userRepo.findById(id).orElseThrow(() -> new RuntimeException("用户不存在"));
        db.setRealName(user.getRealName());
        db.setPhone(user.getPhone());
        db.setEmail(user.getEmail());
        db.setGender(user.getGender());
        db.setSkills(user.getSkills());
        db.setCollege(user.getCollege());
        db.setRole(user.getRole() == null ? db.getRole() : user.getRole());
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            db.setPassword(user.getPassword());
        }
        if (user.getStatus() != null) db.setStatus(user.getStatus());
        return R.ok("更新成功", userRepo.save(db));
    }

    /** 启用/禁用 */
    @PutMapping("/{id}/toggle")
    public R<User> toggle(@PathVariable Long id) {
        User db = userRepo.findById(id).orElseThrow(() -> new RuntimeException("用户不存在"));
        db.setStatus(db.getStatus() != null && db.getStatus() == 1 ? 0 : 1);
        return R.ok(userRepo.save(db));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        userRepo.deleteById(id);
        return R.ok();
    }

    /** 维修工技能信息维护（本人或管理员） */
    @PutMapping("/{id}/skills")
    public R<User> updateSkills(@PathVariable Long id, @RequestParam String skills) {
        User db = userRepo.findById(id).orElseThrow(() -> new RuntimeException("用户不存在"));
        db.setSkills(skills);
        return R.ok("技能已更新", userRepo.save(db));
    }
}
