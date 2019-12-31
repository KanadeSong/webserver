package com.seater.smartmining.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/6 0006 11:37
 */
@Data
public class CubicDetailByIntegration {

    private Long projectId = 0L;
    private List<CubicDetail> detailList = new ArrayList<>();
    private List<CubicDetailElse> detailElseList = new ArrayList<>();
    private Date reportDate = null;
    private Long machineId = 0L;
}
