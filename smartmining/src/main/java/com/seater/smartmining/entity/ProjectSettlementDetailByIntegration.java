package com.seater.smartmining.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/6 0006 9:45
 */
@Data
public class ProjectSettlementDetailByIntegration {

    private Long projectId = 0L;

    private Long carId = 0L;    //渣车编号

    private Long totalId = 0L;  //合计的主键编号

    private Long thirtyFive = 0L;

    private List<SettlementDetail> detailList = new ArrayList<>();

    private List<SettlementSummary> summaryList = new ArrayList<>();

    private Date reportDate = null;     //统计日期
}
