package com.seater.smartmining.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:临时查询日报表
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/9/27 0027 16:57
 */
@Data
public class ProjectTempDayReport {

    private Long carId = 0L;        //渣车ID

    private String carCode = "";        //渣车编号

    private Long totalDistance = 0L;        //总运距

    private Integer[] earlyDetailDistance = null;           //早班运距详情

    private Integer[] nightDetailDistance = null;           //晚班运距详情

    private Integer earlyCount = 0;         //早班车数

    private Integer nightCount = 0;         //晚班车数

    private Integer totalCount = 0;         //总车数

    private Long carOwnerId = 0L;           //渣车车主ID

    private String carOwnerName = "";       //渣车车主名称

    private Long amount = 0L;           //总金额

    private Long cubic = 0L;            //总方量

    private BigDecimal avgFill = BigDecimal.ZERO;       //车/升

    private Long fillCount = 0L;         //加油量

    private Long fillAmount = 0L;        //加油金额

    private Long shouldPayAmount = 0L;       //应付 金额

    private BigDecimal percentOfUsing = BigDecimal.ZERO;        //油耗
}
