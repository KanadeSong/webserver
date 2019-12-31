package com.seater.smartmining.entity;

import lombok.Data;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/6 0006 11:42
 */
@Data
public class CubicDetailElse {

    private Long carsByTemp = 0L;   //临时车数

    private Long cubicByTemp = 0L;  //临时方量

    private Long carsByTotal = 0L;  //合计车数

    private Long cubicByTotal = 0L;     //合计方量

    private Long price = 0L;    //单价

    private Long amount = 0L;   //金额

    private Long oilCount = 0L; //油量

    private Long amountByOil = 0L;  //加油金额

}
