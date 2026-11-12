package cn.edu.sdua.sushe.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 维修工工时排班。
 */
@Data
@Entity
@Table(name = "worker_schedule")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long workerId;

    private String workerName;

    /** 排班日期 yyyy-MM-dd */
    private String workDate;

    /** 班次：早班/中班/晚班/休息 */
    private String shift;

    /** 该班次工时（小时） */
    private Double hours;

    /** 状态：待值班/值班中/已完成 */
    private String status;
}
