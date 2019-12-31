package com.seater.smartmining.entity;

import com.seater.smartmining.enums.StatisticsTypeEnums;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/2/22 0022 11:15
 */
@Entity
@Table
@Data
public class ProjectCostAccountingCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long projectId = 0L;            //项目编号

    @Column
    private Long totalAmount = 0L;              //生产总费用

    @Column
    private Long costBySingleHook = 0L;        //单位成本  分/立方

    @Column
    private Long measure = 0L;                  //测量方

    @Column
    private Long coefficient = 0L;              //系数

    @Column
    private StatisticsTypeEnums statisticsType = StatisticsTypeEnums.UNKNOW;    //统计类型

    @Column
    private Date reportDate;                //测量日期

    @Column(columnDefinition = "TEXT")
    private String remark;

}
