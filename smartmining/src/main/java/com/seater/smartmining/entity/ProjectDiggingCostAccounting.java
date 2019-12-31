package com.seater.smartmining.entity;

import com.seater.smartmining.enums.PricingTypeEnums;
import com.seater.smartmining.enums.StatisticsTypeEnums;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:成本核算表
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/2/20 0020 16:22
 */
@Entity
@Table
@Data
public class ProjectDiggingCostAccounting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;            //主键编号

    @Column
    private Long projectId = 0L;     //项目编号

    @Column(columnDefinition = "DOUBLE")
    private BigDecimal oilConsumption = new BigDecimal(0);          //油耗比

    @Column
    private Long totalCountByTimer = 0L;    //计时挖机车数

    @Column
    private Long totalCubicByTimer = 0L;      //计时总方量数

    @Column
    private BigDecimal workTimeByTimer = new BigDecimal(0);         //计时台时

    @Column
    private Long amountByTimer = 0L;       //计时金额

    @Column
    private Long fillCountByTimer = 0L;        //计时油量

    @Column
    private Long amountByFillByTimer = 0L;     //计时用油金额

    @Column
    private Long totalAmountByTimer = 0L;      //计时总费用

    @Column
    private BigDecimal workTimeBySingleHook = new BigDecimal(0);     //单勾台时

    @Column
    private Long amountBySingleHook = 0L;       //单勾金额

    @Column
    private Long fillCountBySingleHook = 0L;    //单勾油量

    @Column
    private Long amountByFillBySingleHook = 0L;     //单勾用油金额

    @Column
    private Long totalAmountBySingleHook = 0L;      //单勾总费用

    @Column
    private BigDecimal workTimeByGunHammer = new BigDecimal(0);     //炮锤台时

    @Column
    private Long amountByGunHammer = 0L;       //炮锤金额

    @Column
    private Long fillCountByGunHammer = 0L;    //炮锤油量

    @Column
    private Long amountByFillByGunHammer = 0L;     //炮锤用油金额

    @Column
    private Long totalAmountByGunHammer = 0L;      //炮锤总费用

    @Column
    private BigDecimal workTimeByCubic = new BigDecimal(0);      //包方台时

    @Column
    private Long totalCountByCubic = 0L;    //包方挖机车数

    @Column
    private Long totalCubicByCubic = 0L;      //包方总方量数

    @Column
    private Long fillCountByCubic = 0L;    //包方油量

    @Column
    private Long amountByFillByCubic = 0L;     //包方用油金额

    @Column
    private Long totalAmountByCubic = 0L;      //包方总费用

    @Column
    private Long avgUseFillByTimeByTimer = 0L;         //计时平均耗油 ml/h

    @Column(columnDefinition = "DOUBLE")
    private BigDecimal avgCarsByTimeByTimer = new BigDecimal(0);       //计时平均装车数 车/小时

    @Column
    private Long avgUseFillByTimeBySingleHook = 0L;         //单勾平均耗油 ml/h

    @Column
    private Long unitCostBySingleHook = 0L;        //单勾单位成本

    @Column
    private Long avgUseFillByTimeByGunHammer = 0L;         //炮锤平均耗油 ml/h

    @Column
    private Long unitCostByGunHammer = 0L;        //炮锤单位成本

    @Column
    private Long avgUseFillByTimeByCubic = 0L;         //包方平均耗油 ml/h

    @Column(columnDefinition = "DOUBLE")
    private BigDecimal avgCarsByTimeByCubic = new BigDecimal(0);       //包方平均装车数 车/小时

    @Column
    private Long grossProfitByCubic = 0L;          //毛利润(除油)  分/车

    @Column
    private Long totalAmount = 0L;              //挖机总费用

    @Column
    private Long unitCostByTotal = 0L;        //挖机总的单位成本 分/立方

    @Column
    private Long fillCountByTotal = 0L;    //挖机总用油量

    @Column
    private Long amountByFillByTotal = 0L;     //挖机总用油金额

    @Column
    private StatisticsTypeEnums statisticsType = StatisticsTypeEnums.UNKNOW;    //统计类型

    @Column
    private Date reportDate;                //统计日期
}
