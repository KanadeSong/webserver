package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:挖机日报历史表
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/7 0007 15:17
 */
@Entity
@Table
@Data
public class ProjectDayReportHistory {

    //主键编号
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long projectId = 0L;

    //运输成本 分/立方
    @Column
    private Long cost = 0L;

    //油耗比
    @Column
    private BigDecimal oilConsumption = new BigDecimal(0);

    //完成车数
    @Column
    private Integer finishCars = 0;

    //完成方量
    @Column
    private Long finishCubic = 0L;

    //总金额
    @Column
    private Long totalAmount = 0L;

    //加油量
    @Column
    private Long fillCount = 0L;

    //用油金额
    @Column
    private Long fillAmount = 0L;

    //应付金额
    @Column
    private Long shouldPay = 0L;

    //平均用油  毫升/车
    @Column
    private Long avgOil = 0L;

    //毛利润
    @Column
    private Long grossProfit = 0L;

    //平均次数(趟/天)
    @Column
    private BigDecimal avgCarByTime = new BigDecimal(0);

    //出煤车数
    @Column
    private Integer carsCountByCoal = 0;

    //排渣总里程
    @Column
    private Long distance = 0L;

    //平均里程
    @Column
    private Long avgDistance = 0L;

    @Column
    private Date createTime = new Date(0);

    @Column
    private Date reportDate = new Date(0);
}
