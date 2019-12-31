package com.seater.smartmining.mqtt.domain;

import lombok.Data;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/12 0012 11:38
 */
@Data
public class OtherDeviceOnOff {
    private String cmdInd = "";
    private Long pktID = 0L;
    private Long cmdStatus = 0L;
    private Long projectID = 0L;
    private Long otherDeviceID = 0L;
    private Integer status = 0;
}
