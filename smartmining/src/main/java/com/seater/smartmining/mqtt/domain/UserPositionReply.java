package com.seater.smartmining.mqtt.domain;

import lombok.Data;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/12/12 0012 12:48
 */
@Data
public class UserPositionReply {

    private String cmdInd;
    private String account;
    private Long cmdStatus;
    private String message;
}
