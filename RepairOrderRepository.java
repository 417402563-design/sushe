package cn.edu.sdua.sushe.repository;

import cn.edu.sdua.sushe.entity.RepairOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepairOrderRepository extends JpaRepository<RepairOrder, Long> {
    List<RepairOrder> findByStudentIdOrderByCreateTimeDesc(Long studentId);

    List<RepairOrder> findByWorkerIdOrderByCreateTimeDesc(Long workerId);

    List<RepairOrder> findByStatusOrderByCreateTimeDesc(String status);

    long countByStatus(String status);
}
