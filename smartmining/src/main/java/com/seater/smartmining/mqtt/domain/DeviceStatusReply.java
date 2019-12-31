package com.seater.smartmining.mqtt.domain;

import com.seater.smartmining.entity.ProjectDeviceStatus;
import com.seater.smartmining.enums.DeviceStartStatusEnum;
import com.seater.smartmining.enums.ProjectDeviceType;
import lombok.Data;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/5 0005 17:12
 */
@Data
public class DeviceStatusReply {

    private String cmdInd;
    private Long pktID;
    private String uid;
    private Long projectID;
    private String carCode;
    private ProjectDeviceType projectDeviceType;
    private ProjectDeviceStatus status;
}
