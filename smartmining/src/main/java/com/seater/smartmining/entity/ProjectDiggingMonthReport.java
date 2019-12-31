package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:挖机月度报表实体类
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/1/28 0028 18:10
 */
@Entity
@Table
@Data
public class ProjectDiggingMonthReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long totalId = 0L;

    //项目编号
    @Column
    private Long projectId = 0L;

    //挖机编号
    @Column
    private Long machineId = 0L;

    //机械编号
    @Column
    private String machineCode = "";

    //机械名称
    @Column
    private String machineName = "";

    @Column
    private Long ownerId = 0L;

    //机主名称
    @Column
    private String workerName = "";

    //本月累计总工时
    @Column
    private BigDecimal granWorkCountTime = new BigDecimal(0);

    //本月累计计时工时
    @Column
    private BigDecimal grandWorkTimeByTimer = new BigDecimal(0);

    //包时单价
    @Column
    private Long singlePrice =0L;

    //本月累计计时金额
    @Column
    private Long grandTimerAmount = 0L;

    //本月累计计方工时
    @Column
    private BigDecimal granWorkTimeByCubic = new BigDecimal(0);

    //本月包方车数
    @Column
    private Long totalCount = 0L;

    //本月累计总方量
    @Column
    private Long grandTotalCubic = 0L;

    //本月累计计方金额
    @Column
    private Long grandCubeAmout = 0L;

    //本月累计计时加油量
    @Column
    private Long grandTotalFillByTimer = 0L;

    //本月累计计方加油量
    @Column
    private Long grandTotalFillByCubic = 0L;

    //本月累计加油量
    @Column
    private Long grandTotalFill = 0L;

    //本月累计计时用油金额
    @Column
    private Long grandUsingFillByTimer = 0L;

    //本月累计计方用油金额
    @Column
    private Long grandUsingFillByCubic = 0L;

    //本月累计用油金额
    @Column
    private Long grandUsingFill = 0L;

    //包方结余金额
    @Column
    private Long payAmount = 0L;

    //本月累计工作总金额
    @Column
    private Long workTotalAmount = 0L;

    //包月/元
    @Column
    private  Long monthAmount = 0L;

    //补贴
    @Column
    private Long subsidyAmount =0L;

    //扣款
    @Column
    private Long deduction = 0L;

    //结算总金额
    @Column
    private Long settlementAmount = 0L;

    //应付金额
    @Column
    private Long shouldPayAmount = 0L;

    //平均耗油量
    @Column(columnDefinition = "DOUBLE")
    private BigDecimal avgUseFill = new BigDecimal(0);

    //平均车辆
    @Column(columnDefinition = "DOUBLE")
    private BigDecimal avgCar = new BigDecimal(0);

    //毛利润
    @Column(columnDefinition = "DOUBLE")
    private BigDecimal grossProfit = new BigDecimal(0);

    //油耗
    @Column(columnDefinition = "DOUBLE")
    private BigDecimal oilConsumption = new BigDecimal(0);

    //报表日期
    @Column
    private Date reportDate = null;

}
