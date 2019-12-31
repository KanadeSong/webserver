package com.seater.smartmining.mqtt.domain;

import lombok.Data;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/4/11 0011 18:21
 */
@Data
public class ScheduledReplyByCar {

    private String cmdInd = "";
    private Long pktID = 0L;
    private Long cmdStatus = 0L;
    private Long projectID = 0L;        //项目ID
    private Long slagcarID = 0L;        //渣车ID
    private String excavatorID = "";    //挖机ID
    private String excavatorCode = "";  //挖机编号
    private String slagSiteID = "";     //渣场ID 多个用逗号隔开
    private String position = "";       //经纬度  经度-纬度 多个用逗号隔开
    private String priceMethod = "";        // 计价方式 1 - 计时  2 - 计方   3 - 计时计方，多个用逗号隔开
    private Integer schMode = 0;  // 排班类型   1 - 挖机排班   2 - 完整混装
                                           // 3 - 分组混装  4 - 智能调度
    private String exctDist = "";           //运距 多个用逗号隔开
    private String loader = "";         //物料，多个用逗号隔开
    private Integer dispatchMode = 0;       //调度模式
}
