package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/6/11 0011 16:11
 */
@Entity
@Table
@Data
public class ProjectDiggingDayReportHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键ID

    @Column
    private Long projectId = 0L;        //项目ID

    @Column
    private Long totalTimeByTimer = 0L;         //计时时长

    @Column
    private Long totalTimeByCubic = 0L;         //计方时长

    @Column
    private Long totalTime = 0L;            //累计总时长

    @Column
    private Integer totalCountByTimer = 0;      //计时车数

    @Column
    private Integer totalCountByCubic = 0;      //计方车数

    @Column
    private Integer totalCount = 0;         //累计总车数

    @Column
    private Long totalCubicByTimer = 0L;        //计时总方量

    @Column
    private Long totalCubicByCubic = 0L;        //计方总方量

    @Column
    private Long totalCubic = 0L;           //累计总方量

    @Column
    private Long totalFillByTimer = 0L;         //累计计时加油量

    @Column
    private Long totalFillByCubic = 0L;         //累计计方加油量

    @Column
    private Long totalFill = 0L;        //累计加油量

    @Column
    private Long totalFillAmountByTimer = 0L;       //累计计时加油金额

    @Column
    private Long totalFillAmountByCubic = 0L;       //累计计方加油金额

    @Column
    private Long totalFillAmount = 0L;      //累计总加油金额

    @Column
    private Long totalAmountByTimer = 0L;       //计时总金额

    @Column
    private Long totalAmountByCubic = 0L;       //计方总金额

    @Column
    private Long shouldAmount = 0L;         //应付金额

    @Column
    private Long totalAmount = 0L;      //总金额

    @Column
    private Long useFill = 0L;          //耗油，毫升/小时

    @Column
    private BigDecimal avgCars = new BigDecimal(0);     //车/小时

    private BigDecimal avgCarsByTimer = new BigDecimal(0);      //todo 车/小时 计时

    private BigDecimal avgCarsByCubic = new BigDecimal(0);      //todo 车/小时 计方

    @Column
    private Long avgCubic = 0L;     //平均方量 立方厘米/小时

    @Column
    private Long avgAmountByFill = 0L;        //平均金额 分/车(含油)

    @Column
    private Long avgAmount = 0L;            //平均金额 分/车(不含油)

    @Column
    private BigDecimal oilConsumption = new BigDecimal(0);      //油耗比

    @Column
    private Date createDate = null;

    @Column
    private Date reportDate = null;
}
