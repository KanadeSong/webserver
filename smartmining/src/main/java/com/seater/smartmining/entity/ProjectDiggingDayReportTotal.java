package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/2/15 0015 14:52
 */
@Entity
@Table
@Data
public class ProjectDiggingDayReportTotal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;    //主键

    @Column
    private Long projectId;         //项目编号

    @Column
    private BigDecimal subtotalByTimer = new BigDecimal(0);     //计时小计

    @Column
    private BigDecimal totalTimeByTimer = new BigDecimal(0);    //计时工时

    @Column
    private Long amountByTimer = 0L;    //计时金额

    //计时车数
    @Column
    private Long totalCountByTimer = 0L;

    //计时方量
    @Column
    private Long cubicCountByTimer = 0L;

    //计时平均车数
    @Column(columnDefinition = "DOUBLE")
    private BigDecimal avgCarByTimer = new BigDecimal(0);

    @Column
    private BigDecimal subtotalByCubic = new BigDecimal(0);    //包方小计

    @Column
    private BigDecimal totalTimeByCubic = new BigDecimal(0);    //包方工时

    @Lob
    @Column(columnDefinition="text")
    private String cubicDetail = "";    //物料运送详情

    @Column
    private Long carTotalCountByCubic = 0L;   //包方总车数

    @Column
    private Long carTotalCountByDay = 0L;       //白班总车数

    @Column
    private Long carTotalCountByNight = 0L;     //晚班总车数

    @Column
    private Long totalCountByCubic = 0L;    //总方量

    @Column
    private Long totalCountByDay = 0L;      //白班总方量

    @Column
    private Long totalCountByNight = 0L;        //晚班总方量

    @Column
    private Long totalAmountByCubic = 0L;    //包方总金额

    @Column
    private BigDecimal totalWorkTimerByDay = new BigDecimal(0);     //白班总工时

    @Column
    private BigDecimal totalWorkTimerByNight = new BigDecimal(0);   //晚班总工时

    @Column
    private BigDecimal totalWorkTimer = new BigDecimal(0);    //总工时

    @Column
    private Long totalAmount = 0L;   //总金额

    @Column
    private Long totalAmountByDay = 0L;     //白班总金额

    @Column
    private Long totalAmountByNight = 0L;       //晚班总金额

    @Column
    private Long totalGrandFillByTimer = 0L;        //计时总加油量

    @Column
    private Long totalGrandFillByCubic = 0L;            //计方总加油量

    //总加油量
    @Column
    private Long totalGrandFill = 0L;

    @Column
    private Long totalAmountByFillByTimer = 0L;         //计时加油金额

    @Column
    private Long totalAmountByFillByCubic = 0L;         //计方加油金额

    //总的加油金额
    @Column
    private Long totalAmountByFill = 0L;

    //结余金额
    @Column
    private Long shouldPayAmount = 0L;

    //平均耗油量
    @Column(columnDefinition = "DOUBLE")
    private BigDecimal avgUseFill = new BigDecimal(0);

    //平均车辆
    @Column(columnDefinition = "DOUBLE")
    private BigDecimal avgCar = new BigDecimal(0);

    //平均方数
    @Column(columnDefinition = "DOUBLE")
    private BigDecimal avgCubics = new BigDecimal(0);

    //平均价格
    @Column(columnDefinition = "DOUBLE")
    private BigDecimal avgAmount = new BigDecimal(0);

    //油耗
    @Column(columnDefinition = "DOUBLE")
    private BigDecimal oilConsumption = new BigDecimal(0);

    @Column
    private BigDecimal grandTimeByTimer = new BigDecimal(0);          //计时累计台时

    @Column
    private Long grandPriceByTimer = 0L;               //计时单价

    @Column
    private Long grandAmountByTimer = 0L;             //计时总价

    //计时车数
    @Column
    private Long grandTotalCountByTimer = 0L;

    //计时方量
    @Column
    private Long grandCubicCountByTimer = 0L;

    //计时平均车数
    @Column(columnDefinition = "DOUBLE")
    private BigDecimal grandAvgCarByTimer = new BigDecimal(0);

    @Column
    private BigDecimal grandTimeByCubic = new BigDecimal(0);          //计方累计台时

    @Lob
    @Column(columnDefinition="text")
    private String grandCubicDetail = "";              //计方物料运输累计详情

    @Column
    private Long countCarsByCubic = 0L;                  //累计包方总车数

    @Column
    private Long countCarsByDay = 0L;       //累计白班总车数

    @Column
    private Long countCarsByNight = 0L;     //累计晚班总车数

    @Column
    private Long countCubic = 0L;                 //累计总方量

    @Column
    private Long countCubicByDay = 0L;      //累计白班总方量

    @Column
    private Long countCubicByNight = 0L;        //累计晚班总方量

    @Column
    private Long countAmountByCubic = 0L;                //累计包方总金额

    @Column
    private Long countAmountByDay = 0L;     //累计白班总金额

    @Column
    private Long countAmountByNight = 0L;       //累计晚班总金额

    @Column
    private BigDecimal countTimer = new BigDecimal(0);                 //累计总台时

    @Column
    private Long grandWorkAmount = 0L;            //累计工作总金额

    @Column
    private Long grandTotalGrandFillByTimer = 0L;       //累计计时加油量

    @Column
    private Long grandTotalGrandFillByCubic = 0L;       //累计计方加油量

    @Column
    private Long grandTotalGrandFill = 0L;             //累计加油量

    @Column
    private Long grandTotalAmountByFillByTimer = 0L;        //累计白班加油金额

    @Column
    private Long grandTotalAmountByFillByCubic = 0L;        //累计晚班加油金额

    @Column
    private Long grandTotalAmountByFill = 0L;          //累计加油金额

    @Column
    private Long grandShouldPayAmount = 0L;            //累计结余金额

    @Column(columnDefinition = "decimal")
    private BigDecimal grandAvgUseFill = new BigDecimal(0);   //累计平均耗油量

    @Column(columnDefinition = "DOUBLE")
    private BigDecimal grandAvgCar = new BigDecimal(0);      //累计平均车辆

    @Column(columnDefinition = "DOUBLE")
    private BigDecimal grandAvgCubics = new BigDecimal(0);     //累计平均方数

    @Column(columnDefinition = "DOUBLE")
    private BigDecimal grandAvgAmount = new BigDecimal(0);     //累计平均价格

    @Column(columnDefinition = "DOUBLE")
    private BigDecimal grandOilConsumption = new BigDecimal(0);   //累计油耗

    //报表日期
    @Column
    private Date reportDate = null;
}
