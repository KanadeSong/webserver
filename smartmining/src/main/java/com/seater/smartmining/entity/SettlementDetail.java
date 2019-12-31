package com.seater.smartmining.entity;

import lombok.Data;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/6 0006 9:54
 */
@Data
public class SettlementDetail {

    private Long detailId = 0L;   //主键编号

    private Long distance = 0L;     //运距

    private Long materialId = 0L;     //物料编号

    private String materialName = "";       //物料名称

    private Integer carsCount = 0;    //车数

    private Long cubicCount = 0L;   //方量

    private Long price = 0L;    //油单价

    private Long amount = 0L;   //金额
}
