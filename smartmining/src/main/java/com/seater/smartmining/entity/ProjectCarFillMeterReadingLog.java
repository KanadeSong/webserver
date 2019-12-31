package com.seater.smartmining.entity;

import com.seater.smartmining.enums.ShiftsEnums;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 油车抄表记录表
 */
@Data
@Entity
public class ProjectCarFillMeterReadingLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column(nullable = false)
    private Long projectId = null;    //参与的项目编号

    @Column(nullable = false)
    private Date addTime = new Date();           //添加时间

    @Column(nullable = false)
    private Boolean isVaild = true; //是否有效

    @Column
    private Long startOilMeterToday = 0L;    //当日油表初始数(单位:毫升)

    @Column
    private Long endOilMeterToday = 0L;      //当日油表终止数(单位:毫升)

//    @Column
//    private Long oilMeterToday = 0L;        //当日加油升数(单位:毫升)   暂时用不到

    @Column
    private Long oilMeterTodayTotal = 0L;    //当日油表合计数(单位:毫升)

//    @Column
//    private Long oilFillHistory = 0L;   //  历史加油升数(单位:毫升)   暂时用不到

    @Column(nullable = true)
    private Integer port;      //  油枪端口

    @Column(nullable = true)
    private Long oilCarId = 0L;      //  油车id

    @Column(nullable = true)
    private String oilCarCode = "";  //  油车编号
    
    @Column
    private Date updateDate = new Date();   //  更新日期
    
    @Column
    private Long operatorId = 0L;   //  抄表人id
    
    @Column
    private String operatorName;    //  抄表人姓名
    
    @Column
    private ShiftsEnums shifts = ShiftsEnums.DAYSHIFT;    //  班次 默认在早班自动抄表,修改的时候拿修改时间的班次

    @Column
    private Date dateIdentification = null;  //  当班日期(当前班次对应日期)
}