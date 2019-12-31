package com.seater.smartmining.mqtt.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/8/21 0021 15:36
 */
@Data
public class CurrentDataByDigging {

    private String cmdInd = "";
    private Long pktID = 0L;
    private Long cmdStatus = 0L;
    private Long projectID = 0L;
    private Long excavatorID = 0L;
    private String shift = "";
    private String numMat = "";
    private Long numTol = 0L;
    private Long placeId = 0L;
    private String placeName = "";
    private Long workTime = 0L;
}
