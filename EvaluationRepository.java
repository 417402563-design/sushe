package cn.edu.sdua.sushe.repository;

import cn.edu.sdua.sushe.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findByWorkerIdOrderByCreateTimeDesc(Long workerId);

    List<Evaluation> findByStudentIdOrderByCreateTimeDesc(Long studentId);

    Evaluation findByOrderId(Long orderId);
}
