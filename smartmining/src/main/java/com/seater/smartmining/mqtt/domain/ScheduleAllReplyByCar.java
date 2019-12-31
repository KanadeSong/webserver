package com.seater.smartmining.mqtt.domain;

import lombok.Data;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/23 0023 14:53
 */
@Data
public class ScheduleAllReplyByCar {

    private String cmdInd = "";
    private Long pktID = 0L;
    private Long cmdStatus = 0L;
    private Long projectID = 0L;        //项目ID
    private Long slagcarID = 0L;        //渣车ID
    private String exctID = "";    //挖机ID
    private String exctCode = "";  //挖机编号
    private String exctPos = "";        //挖机坐标
    private String exctDist = "";           //运距 多个用逗号隔开
    private String exctWait = "";           //该挖机对应渣车的等待数
    private String exctPlace = "";          //挖机工作平台
    private String exctPrice = "";          //挖机计价方式 计时，计方
    private String exctPriceID = "";        //挖机计价方式ID
    private String exctLoader = "";     //挖机对应的物料
    private String exctLoaderID = "";       //挖机对应的物料ID
    private String exctStatus = "";     //挖机状态
    private String slagSiteID = "";     //渣场ID 多个用逗号隔开
    private String slagSiteName = "";       //渣场名称
    private String slagSitePos = "";        //渣场经纬度
    private String project = "";        //项目名称
    private String manager = "";        //管理员
    private Integer schMode = 0;  // 排班类型   1 - 挖机排班   2 - 完整混装
    // 3 - 分组混装  4 - 智能调度
    private Integer dispatchMode = 0;       //调度模式

    private String message = "";        //请求信息
}
