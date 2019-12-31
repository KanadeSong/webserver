package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/2/19 0019 10:25
 */
@Entity
@Table
@Data
public class ProjectMonthReportTotal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键编号

    @Column
    private Long projectId = 0L;        //项目编号

    @Column
    private Integer totalCount = 0;       //总车数

    @Column
    private Long totalCubic = 0L;           //总方量

    @Column
    private Long totalAmount = 0L;          //总金额

    //补贴金额
    @Column
    private Long subsidyAmount =0L;

    @Column
    private Long totalFill = 0L;            //总加油量

    @Column
    private Long totalAmountByFill = 0L;        //总加油金额

    //扣款
    @Column
    private Long deduction = 0L;

    @Column
    private Long shouldPayAmount = 0L;          //应付金额

    @Column
    private Long avgAmount = 0L;              //平均价格(不含油)

    @Column
    private Long avgAmountByFill = 0L;          //平均价格(含油)

    @Column
    private Long distance = 0L;             //里程数

    @Column(columnDefinition = "DOUBLE")
    private BigDecimal oilConsumption = new BigDecimal(0);          //油耗

    @Column
    private Long avgFill = 0L;           //油量

    @Column
    private Integer onDutyCount = 0;           //出勤数

    @Column(columnDefinition = "DOUBLE")
    private BigDecimal unitCost = new BigDecimal(0);        //单位成本

    @Column(columnDefinition = "DOUBLE")
    private BigDecimal avgCubics = new BigDecimal(0);            //平均方量

    @Column
    private BigDecimal grandAvgCountsPerCarPerDay = new BigDecimal(0);         //每天每部车每天车数

    @Column
    private Date reportDate = null;         //统计日期
}
