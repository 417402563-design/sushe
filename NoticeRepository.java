package cn.edu.sdua.sushe.repository;

import cn.edu.sdua.sushe.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findAllByOrderByTopDescPublishTimeDesc();
}
