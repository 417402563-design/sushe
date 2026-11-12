package cn.edu.sdua.sushe.repository;

import cn.edu.sdua.sushe.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByBuilding(String building);
}
