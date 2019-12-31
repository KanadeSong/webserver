package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/2/16 0016 13:12
 */
@Entity
@Table
@Data
public class ProjectDiggingMonthReportTotal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;               //主键编号

    @Column
    private Long projectId = 0L;        //项目编号

    @Column
    private BigDecimal granWorkCountTime =new BigDecimal(0);         //总工时

    @Column
    private BigDecimal grandWorkTimeByTimer = new BigDecimal(0);        //计时台时

    @Column
    private Long singlePrice =0L;               //计时单价

    @Column
    private Long grandTimerAmount = 0L;         //计时金额

    @Column
    private BigDecimal granWorkTimeByCubic = new BigDecimal(0);      //包方台时

    @Column
    private Long totalCount = 0L;           //包方车数

    @Column
    private Long grandTotalCubic = 0L;      //包方方量

    @Column
    private Long grandCubeAmout = 0L;       //包方金额

    @Column
    private Long grandTotalFillByTimer = 0L;        //计时用油量

    @Column
    private Long grandTotalFillByCubic = 0L;    //计方用油量

    @Column
    private Long grandTotalFill = 0L;       //用油量

    @Column
    private Long grandUsingFillByTimer = 0L;        //计时用油金额

    @Column
    private Long grandUsingFillByCubic = 0L;        //计方用油金额

    @Column
    private Long grandUsingFill = 0L;       //用油金额

    @Column
    private Long payAmount = 0L;            //包方结余金额

    @Column
    private  Long monthAmount = 0L;         //包月/元

    @Column
    private Long workTotalAmount = 0L;      //工作总金额

    @Column
    private Long subsidyAmount =0L;         //补贴金额

    @Column
    private Long settlementAmount = 0L;     //结算总金额

    @Column
    private Long deduction = 0L;            //扣款金额

    @Column
    private Long shouldPayAmount = 0L;      //应付总金额

    @Column(columnDefinition = "DOUBLE")
    private BigDecimal avgUseFill = new BigDecimal(0);           //平均用油量

    @Column(columnDefinition = "DOUBLE")
    private BigDecimal avgCar = new BigDecimal(0);               //平均车辆

    @Column(columnDefinition = "DOUBLE")
    private BigDecimal grossProfit = new BigDecimal(0);          //毛利润

    @Column(columnDefinition = "DOUBLE")
    private BigDecimal oilConsumption = new BigDecimal(0);      //油耗

    @Column
    private Date reportDate = null;

    @Column
    private Integer onDutyCount = 0;        //出勤数

    @Column(columnDefinition = "DOUBLE")
    private BigDecimal unitCost = new BigDecimal(0);        //单位成本

    @Column
    private Long totalFill = 0L;            //当月总用油量

    @Column
    private Long totalFillAmount = 0L;          //当月用油金额
}
