package cn.edu.sdua.sushe.repository;

import cn.edu.sdua.sushe.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByWorkerIdOrderByWorkDateAsc(Long workerId);
}
