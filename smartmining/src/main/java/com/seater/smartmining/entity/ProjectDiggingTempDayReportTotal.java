package com.seater.smartmining.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/9/29 0029 15:14
 */
@Data
public class ProjectDiggingTempDayReportTotal {

    private BigDecimal workTimeByTimer = BigDecimal.ZERO;          //计时工作时长

    private BigDecimal priceByTimer = BigDecimal.ZERO;      //计时单价

    private BigDecimal amountByTimer = BigDecimal.ZERO;     //计时金额

    private Integer carCountByTimer = 0;            //计时车数

    private BigDecimal cubicByTimer = BigDecimal.ZERO;      //计时方量

    private BigDecimal avgCarsByTimer = BigDecimal.ZERO;        //计时 车/小时

    private BigDecimal workTimeByCubic = BigDecimal.ZERO;           //计方工作时长

    private Integer carCountByCubic = 0;        //计方车数

    private BigDecimal cubicByCubic = BigDecimal.ZERO;          //计方方量

    private BigDecimal amountByCubic = BigDecimal.ZERO;         //计方金额

    private String detailJson = "";         //包方详情

    private BigDecimal workTime = BigDecimal.ZERO;      //总工时

    private BigDecimal amount = BigDecimal.ZERO;        //总金额

    private BigDecimal fillCount = BigDecimal.ZERO;     //总用量

    private BigDecimal amountByFill = BigDecimal.ZERO;      //总用油金额

    private BigDecimal shouldPayAmount = BigDecimal.ZERO;       //应付金额

    private BigDecimal avgFillCount = BigDecimal.ZERO;          //升/小时

    private BigDecimal avgCarsCount = BigDecimal.ZERO;          //车/小时

    private BigDecimal avgAmount = BigDecimal.ZERO;             //元/车(不含油)

    private BigDecimal oilConsumption = BigDecimal.ZERO;        //油耗
}
