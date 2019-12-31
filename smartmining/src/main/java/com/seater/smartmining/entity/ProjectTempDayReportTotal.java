package com.seater.smartmining.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/9/27 0027 17:53
 */
@Data
public class ProjectTempDayReportTotal {

    private Long totalDistance = 0L;        //总运距

    private String detailDistance = "";           //运距详情

    private Integer earlyCount = 0;         //早班车数

    private Integer nightCount = 0;         //晚班车数

    private Integer totalCount = 0;         //总车数

    private Long amount = 0L;           //总金额

    private Long cubic = 0L;            //总方量

    private BigDecimal avgFill = BigDecimal.ZERO;       //车/升

    private Long fillCount = 0L;         //加油量

    private Long fillAmount = 0L;        //加油金额

    private Long shouldPayAmount = 0L;       //应付 金额

    private BigDecimal percentOfUsing = BigDecimal.ZERO;        //油耗

    private Integer carCount = 0;       //注册总数

    private BigDecimal avgCars = BigDecimal.ZERO;       //每车平均

    private Integer onDutyCount = 0;        //总出勤数

    private Integer earlyOnDutyCount = 0;   //早班出勤数

    private BigDecimal earlyAttendance = BigDecimal.ZERO;    //早班出勤(0.00%)

    private Integer nightOnDutyCount = 0;   //晚班出勤数

    private BigDecimal nightAttendance = BigDecimal.ZERO;    //晚班出勤(0.00%)

    private Integer coalCount = 0;          //煤车数

    private Long grossProfit = 0L;        //毛利(分)
}
