package com.seater.smartmining.entity;

import com.seater.smartmining.enums.StatisticsTypeEnums;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/2/21 0021 18:03
 */
@Entity
@Table
@Data
public class ProjectCarCostAccounting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;            //主键编号

    @Column
    private Long projectId = 0L;        //项目编号

    @Column
    private Long totalCount = 0L;       //渣车总车数

    @Column
    private Long totalCubic = 0L;       //渣车总方量

    @Column
    private Long fillCount = 0L;        //总用油量

    @Column
    private Long amountByFill = 0L;     //用油金额

    @Column
    private Long amount = 0L;           //排渣费用

    @Column(columnDefinition = "DOUBLE")
    private BigDecimal oilConsumption = new BigDecimal(0);          //油耗比

    @Column
    private Long costBySingleHook = 0L;        //单位成本  分/立方

    @Column
    private Long grossProfitByCubic = 0L;          //毛利润(除油)  分/车

    @Column
    private Long avgUseFillByCar = 0L;          //平均用油  毫升/车

    @Column
    private StatisticsTypeEnums statisticsType = StatisticsTypeEnums.UNKNOW;    //统计类型

    @Column
    private Date reportDate;                //统计日期
}
