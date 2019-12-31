package com.seater.smartmining.entity;

import lombok.Data;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/6 0006 11:41
 */
@Data
public class CubicDetail {

    private Long carId = 0L;

    private String carCode = "";

    private Long capacity = 0L;             //渣车容量

    private Long materialId = 0L;           //物料主键编号

    private String materialName = "";       //物料名称

    private Long cars = 0L;         //车数

    private Long cubics = 0L;       //方量

    private Long amountByShould = 0L;   //余额


}
