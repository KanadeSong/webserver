package com.seater.smartmining.entity;

import com.seater.smartmining.enums.ShiftsEnums;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/8 0008 0:37
 */
@Entity
@Table
@Data
public class DeductionDigging {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键ID

    @Column
    private Long projectId = 0L;        //项目ID

    @Column
    private BigDecimal deductionTime = new BigDecimal(0);        //扣除时间

    @Column
    private String machineCode = "";        //挖机编号

    @Column
    private Long machineId = 0L;        //挖机ID

    @Column
    private Date reportDate = null;     //统计日期

    @Column
    private ShiftsEnums shifts = ShiftsEnums.UNKNOW;

    @Column
    private BigDecimal timeByDay = new BigDecimal(0);        //白班计时小计

    @Column
    private BigDecimal timeByNight = new BigDecimal(0);       //晚班计时小计

    @Column
    private BigDecimal totalTimeByTimer = new BigDecimal(0);        //计时总时间

    @Column
    private BigDecimal totalTime = new BigDecimal(0);            //总工时

    @Column
    private BigDecimal totalTimeByTimerTotal = new BigDecimal(0);    //合计计时总时间

    @Column
    private BigDecimal totalTimeTotal = new BigDecimal(0);       //合计总工时

    @Column
    private BigDecimal grandTotalTimeByTimer = new BigDecimal(0);    //累计计时总时间

    @Column
    private BigDecimal grandTotalTime = new BigDecimal(0);           //累计总工时

    @Column
    private BigDecimal grandTotalTimeByTimerTotal = new BigDecimal(0);       //累计合计计时总时间

    @Column
    private BigDecimal grandTotalTimeTotal = new BigDecimal(0);      //累计合计总工时

    @Column
    private Long amountByTimer = 0L;        //计时总金额

    @Column
    private Long totalAmount = 0L;          //总金额

    @Column
    private Long totalAmountByTimer = 0L;       //合计计时总金额

    @Column
    private Long totalAmountByTotal = 0L;       //合计总金额

    @Column
    private Long grandAmountByTimer = 0L;       //累计计时总金额

    @Column
    private Long grandTotalAmount = 0L;         //累计总金额

    @Column
    private Long grandTotalAmountByTimer = 0L;      //累计合计计时总金额

    @Column
    private Long grandTotalAmountByTotal = 0L;          //累计合计总金额
}
