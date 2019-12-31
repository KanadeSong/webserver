package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/10 0010 13:11
 */
@Entity
@Table
@Data
public class DeductionDiggingByMonth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键ID

    @Column
    private Long projectId = 0L;    //项目ID

    @Column
    private Long monthReportId = 0L;        //月报表主键ID

    @Column
    private Long machineId = 0L;        //挖机ID

    @Column
    private String machineCode = "";        //挖机编号

    @Column
    private Long amountByDeduction = 0L;        //扣款

    @Column
    private Long amountBySubsidyAmount = 0L;        //补贴

    @Column
    private Long amountByTotal = 0L;       //总金额

    @Column
    private Long amountByTotalByTotal = 0L;     //合计总金额

    @Column
    private Date reportDate = null;         //统计日期

    @Column
    private Date dayReport = null;          //报表的天数日期

}
