package com.seater.smartmining.mqtt.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/12 0012 10:27
 */
@Data
public class ScheduledReply implements Serializable {

    private String cmdInd = "";
    private Long pktID = 0L;
    private Long cmdStatus = 0L;
    private Long excavatorID = 0L;
    private String carID = "";
    private String carCode = "";
    private Long matId = 0L;
    private String matName = "";
    private Integer pmId = 0;
    private String pmName = "";
    private Long projectID = 0L;
    private String placeName = "";
    private String slagSiteName = "";
    private Integer schMode = 0;            //设备模式
    private Integer dispatchMode = 0;
}
