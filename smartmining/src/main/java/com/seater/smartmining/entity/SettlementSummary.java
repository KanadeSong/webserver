package com.seater.smartmining.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/6 0006 11:10
 */
@Data
public class SettlementSummary {

    private Long summaryId = 0L;    //主键编号

    private Long carsCount = 0L;    //车数

    private Long oilCount = 0L;   //油量

    private Long amountByOil = 0L;  //用油金额

    private Long price = 0L;    //单价

    private Long amountByElse = 0L;     //35元/车

    private Long rent = 0L;         //房租

    private Long amountByMeals = 0L;    //伙食费

    private Long subsidyAmount =0L;     //补贴

    private Long balance = 0L;      //余额

    private Long detailId = 0L;     //详情
}
