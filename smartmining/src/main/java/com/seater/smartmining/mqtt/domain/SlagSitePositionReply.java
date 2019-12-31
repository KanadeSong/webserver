package com.seater.smartmining.mqtt.domain;

import lombok.Data;
import java.math.BigDecimal;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/9/15 0015 12:03
 */
@Data
public class SlagSitePositionReply {

    private String cmdInd = "";
    private Long projectId = 0L;
    private Long pktID = 0L;
    private Long cmdStatus = 0L;
    private String slagSiteId = "";
    private String slagSiteName = "";
    private String longitude = "";     //经度
    private String latitude = "";      //纬度
    private String radius = "";           //半径
    private String radiusByPhone = "";        //对讲机的半径
}
