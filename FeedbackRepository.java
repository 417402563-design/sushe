package cn.edu.sdua.sushe.repository;

import cn.edu.sdua.sushe.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByStudentIdOrderByCreateTimeDesc(Long studentId);

    List<Feedback> findByStatusOrderByCreateTimeDesc(String status);
}
