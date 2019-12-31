package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/22 0022 17:06
 */
@Entity
@Table
@Data
public class ProjectCarTotalCountReportByTotal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long projectId = 0L;

    @Column
    private Long totalCount = 0L;       //渣场总车数

    @Column
    private Long totalCountFact = 0L;       //实际总车数

    @Column
    private Long finishCount = 0L;      //完成车数

    @Column
    private BigDecimal finishPercent = BigDecimal.ZERO;     //完成率

    @Column
    private Long exceptionCount = 0L;       //异常车数

    @Column
    private BigDecimal exceptionPercent = BigDecimal.ZERO;      //异常率

    @Column(columnDefinition = "text")
    private String finishDetail = "";       //完成车数详情

    @Column
    private Long successCount = 0L;         //正常合并车数

    @Column
    private Long onlyBySlagSiteSuccessCount = 0L;       //渣场上传 且数据全部正确 合并车数

    @Column
    private Long autoMergeSuccessCount = 0L;        //自动容错 合并车数

    @Column(columnDefinition = "text")
    private String exceptionDetail = "";        //异常车数详情

    @Column
    private Long failByBackStageCount = 0L;      //后台异常车数

    @Column
    private Long deviceUnLineErrorCount = 0L;       //终端离线车数

    @Column
    private Long noHaveDeviceCount = 0L;        //未安装终端

    @Column
    private Long workErrorCount = 0L;       //未按规定卸载

    @Column
    private Long withoutScheduleCount = 0L;     //排班不存在

    @Column
    private Long withoutCarCodeCount = 0L;      //渣车不存在

    @Column
    private Long withoutSlagSiteCodeCount = 0L;     //渣场不存在

    @Column
    private Long scheduleErrorCount = 0L;       //不支持混编

    @Column
    private Long withoutLoaderCount = 0L;       //物料不存在

    @Column
    private Long withoutSlagCarDeviceCount = 0L;        //渣车终端未上传

    @Column
    private Long lostScheduleCount = 0L;        //排班丢失

    @Column
    private Long withoutDiggingMachineCount = 0L;       //挖机不存在

    @Column
    private Long deviceErrorLikeCount = 0L;     //疑似终端异常

    @Column
    private Long recoverWorkInfoFailCount = 0L;     //容错失败

    @Column
    private Shift shift = Shift.Unknown;        //班次

    @Column
    private Date dateIdentification = null;     //日期标识

    @Column
    private Date createTime = new Date();   //创建时间
}
