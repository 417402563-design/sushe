package cn.edu.sdua.sushe.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 宿舍房间信息。
 */
@Data
@Entity
@Table(name = "dorm_room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 楼栋名称 */
    private String building;

    /** 楼层 */
    private Integer floor;

    /** 房间号 */
    private String roomNo;

    /** 宿舍类型：四人间/六人间等 */
    private String dormType;

    /** 容量 */
    private Integer capacity;

    /** 住宿男生/女生 */
    private String genderLimit;

    /** 状态：空闲/住满/维修中 */
    private String status;
}
