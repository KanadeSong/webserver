package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/2/19 0019 9:55
 */
@Entity
@Table
@Data
public class ProjectMonthReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;               //主键编号

    @Column
    private Long projectId = 0L;             //项目编号

    @Column
    private Long totalId = 0L;              //合计报表编号

    @Column
    private Long carId = 0L;                //车辆主键编号

    @Column
    private String code = "";               //车辆编号

    @Column
    private String carOwnerName = "";         //车主名

    @Column
    private Integer totalCount = 0;           //总车数

    @Column
    private Long totalCubic = 0L;           //总方量

    @Column
    private Long totalAmount = 0L;          //总金额

    @Column
    private Long totalFill = 0L;            //总加油量

    @Column
    private Long totalAmountByFill = 0L;        //总加油金额

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
    private Date reportDate = null;                 //统计日期

    @Column
    private Long subsidyAmount =0L;                 //补贴

    @Column
    private Long deduction = 0L;                    //扣款

    @Column
    private Long avgFill = 0L;           //油量
}
