package com.seater.smartmining.mqtt.domain;

import lombok.Data;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/4 0004 19:36
 */
@Data
public class MachineGetPauseReply {

    private String cmdInd;
    private Long excavatorID;
    private Long pktID;
    private Long projectID;
    private String carCode;
    private Integer status;
    private Integer cmdStatus;
    private String message;
}
