package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @Description:炸药表
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/10 0010 17:24
 */
@Entity
@Table
@Data
public class ProjectExplosive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键ID

    @Column
    private String name = "";       //炸药名称

    @Column
    private Long projectId = 0L;        //项目ID

    @Column
    private BigDecimal price = BigDecimal.ZERO;     //单价 元/小时

    @Column
    private String remark = "";     //备注

    @Column
    private Boolean valid = true;       //是否有效
}
