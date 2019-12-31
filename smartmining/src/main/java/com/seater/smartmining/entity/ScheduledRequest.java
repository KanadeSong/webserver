package com.seater.smartmining.entity;

import com.alibaba.fastjson.JSON;
import com.seater.smartmining.enums.PricingTypeEnums;
import lombok.Data;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/4/10 0010 15:27
 */
@Data
public class ScheduledRequest {

    private Long machineId = 0L;        //todo 暂时使用，挖机ID
    private String machineCode = "";    //todo 暂时使用,挖机编号


    private Long[] machineArray = null;     //挖机ID数组
    private String[] machineStrArray = null;        //挖机编号数组
    private Long materialId = 0L;       //物料ID
    private String materialName = "";   //物料名称
    private Long distance = 0L;     //运距
    private PricingTypeEnums pricingType = PricingTypeEnums.Unknow;     //计价方式
    private Long[] carsArray = null;        //渣车ID数组
    private String[] carsStrArray = null;   //渣车编号数组
    private Long diggingMachineBrandId = 0L;
    private Long[] managerId = null;    //队长ID数组
    private String[] managerName = null;    //队长名称数组
    private Long[] employeeId = null;       //组员ID数组
    private String[] employeeName = null;   //组员名称数组
    private String groupCode = "";      //分组编号

}
