package com.seater.smartmining.mqtt.domain;

import lombok.Data;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/12 0012 13:49
 */
@Data
public class GetExcavatorReply {

    private String cmdInd = "";
    private Long pktID = 0L;
    private Long cmdStatus = 0L;
    private Long projectID = 0L;
    private String excavatorID = "";
    private String excavatorNo = "";
    private Long carID = 0L;
}
