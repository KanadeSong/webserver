package com.seater.smartmining.entity;

import com.seater.smartmining.enums.PricingTypeEnums;
import lombok.Data;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/4/10 0010 16:17
 */
@Data
public class ScheduledDiggingMachine {

    private Long machineId = 0L;
    private String machineCode = "";
    private Long materialId = null;         //物料ID
    private String materiaName = null; //物料名称
    private PricingTypeEnums pricingType = PricingTypeEnums.Unknow;     //计价方式
    private Long diggingMachineBrandId = 0L;      //挖机品牌ID
    private Long distance = 0L;         //运距
}
