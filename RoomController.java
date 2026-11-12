package cn.edu.sdua.sushe.controller;

import cn.edu.sdua.sushe.common.R;
import cn.edu.sdua.sushe.entity.Room;
import cn.edu.sdua.sushe.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 宿舍信息管理（管理员增删改，全员可查询用于报修选择）。
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomRepository roomRepo;

    @GetMapping
    public R<List<Room>> list() {
        return R.ok(roomRepo.findAll());
    }

    @GetMapping("/{id}")
    public R<Room> detail(@PathVariable Long id) {
        return R.ok(roomRepo.findById(id).orElse(null));
    }

    @PostMapping
    public R<Room> add(@RequestBody Room room) {
        room.setId(null);
        return R.ok("保存成功", roomRepo.save(room));
    }

    @PutMapping("/{id}")
    public R<Room> update(@PathVariable Long id, @RequestBody Room room) {
        room.setId(id);
        return R.ok("更新成功", roomRepo.save(room));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        roomRepo.deleteById(id);
        return R.ok();
    }
}
