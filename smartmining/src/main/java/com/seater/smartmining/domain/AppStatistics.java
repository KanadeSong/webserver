package com.seater.smartmining.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/4/8 0008 14:58
 */
@Data
public class AppStatistics {

    //挖机出勤数量
    private Integer diggingMachineCount = 0;
    //渣车出勤数量
    private Integer carCount = 0;
    //渣车在线数量
    private Integer carsOnCount = 0;
    //挖机在线数量
    private Integer diggingMachineOnCount = 0;
    //装载异常数量
    private Integer unpass = 0;
    //装载车数
    private Integer carsCount = 0;
    //装载方数 立方厘米
    private Long cubicCount = 0L;
    //平均方量
    private Long avgCubic = 0L;
    //渣车开工率
    private BigDecimal beginPercentByCar = new BigDecimal(0);
    //挖机开工率
    private BigDecimal beginPercentByDigging = new BigDecimal(0);
}
