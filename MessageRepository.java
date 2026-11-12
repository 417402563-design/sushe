package cn.edu.sdua.sushe.repository;

import cn.edu.sdua.sushe.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByToUserIdOrderByCreateTimeAsc(Long toUserId);

    long countByToUserIdAndReadFlagFalse(Long toUserId);

    /** 两人之间的会话消息（在线沟通） */
    @Query("select m from Message m where " +
            "(m.fromUserId = :a and m.toUserId = :b) or (m.fromUserId = :b and m.toUserId = :a) " +
            "order by m.createTime asc")
    List<Message> findConversation(@Param("a") Long a, @Param("b") Long b);
}
