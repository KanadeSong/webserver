package com.seater.smartmining.mqtt.domain;

import lombok.Data;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/4 0004 18:18
 */
@Data
public class GetexctfixReply {

    private String cmdInd;
    private Long pktID;
    private Long projectID;
    private Long slagcarID;
    private Long cmdStatus;
    private String message;
}
