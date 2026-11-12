package cn.edu.sdua.sushe.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 维修工材料申请。
 */
@Data
@Entity
@Table(name = "material_request")
public class MaterialRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long workerId;

    private String workerName;

    private Long partId;

    private String partName;

    private Integer quantity;

    /** 关联工单 */
    private Long orderId;

    private String purpose;

    /** PENDING/APPROVED/REJECTED */
    private String status;

    private String auditRemark;

    private LocalDateTime createTime;

    @PrePersist
    public void prePersist() {
        if (createTime == null) createTime = LocalDateTime.now();
        if (status == null) status = "PENDING";
    }
}
