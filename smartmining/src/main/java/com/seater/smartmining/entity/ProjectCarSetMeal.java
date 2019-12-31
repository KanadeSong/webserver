package com.seater.smartmining.entity;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:车辆套餐服务实体类
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/22 0022 15:17
 */
@Entity
@Table
@Data
public class ProjectCarSetMeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键ID

    @Column
    private Long projectId = 0L;        //项目ID

    @Column
    @NotBlank
    private String name = "";       //套餐名称

    @Column
    @NotNull
    private Integer endDay = 0;     //到期天数

    @Column
    @NotNull
    private BigDecimal price = BigDecimal.ZERO;     //套餐价格

    @Column
    private Boolean valid = true;       //是否有效
}
