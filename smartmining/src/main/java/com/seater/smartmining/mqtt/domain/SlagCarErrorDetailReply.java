package com.seater.smartmining.mqtt.domain;

import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/12/9 0009 15:55
 */
@Data
public class SlagCarErrorDetailReply {

    private String cmdInd;
    private Long pktID;
    private Long projectID;
    private Long cmdStatus;
    private String carCode;
    private String timeDischarge;
    private String message;
    private String remark;
    private String loaderName;
    private String slagSiteName;
    private String errorInfo;
}
