package com.seater.smartmining.mqtt.domain;

import lombok.Data;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/4 0004 19:20
 */
@Data
public class MachinePauseReply {

    private String cmdInd;
    private Long pktID;
    private Long projectID;
    private String carCode;
    private Long excavatorID;
    private Integer cmdStatus;
    private String message;
}
