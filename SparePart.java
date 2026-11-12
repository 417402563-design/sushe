package cn.edu.sdua.sushe.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 备件库存。
 */
@Data
@Entity
@Table(name = "spare_part")
public class SparePart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String partName;

    private String category;

    /** 库存数量 */
    private Integer stock;

    private String unit;

    /** 安全库存下限，低于则预警 */
    private Integer safetyStock;

    private Double price;

    private String location;
}
