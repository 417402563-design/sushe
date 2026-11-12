package cn.edu.sdua.sushe.repository;

import cn.edu.sdua.sushe.entity.MaterialRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialRequestRepository extends JpaRepository<MaterialRequest, Long> {
    List<MaterialRequest> findByWorkerIdOrderByCreateTimeDesc(Long workerId);

    List<MaterialRequest> findByStatusOrderByCreateTimeDesc(String status);
}
