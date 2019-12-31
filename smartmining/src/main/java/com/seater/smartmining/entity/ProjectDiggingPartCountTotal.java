package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/2/28 0028 15:22
 */
@Entity
@Table
@Data
public class ProjectDiggingPartCountTotal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键编号

    @Column
    private Long projectId = 0L;   //项目编号

    @Column
    private Long machineId = 0L;    //挖机主键编号

    @Column
    private String machineCode = "";    //挖机编号

    @Column
    private BigDecimal workTimeByTimer = new BigDecimal(0);      //包时工时

    @Column
    private BigDecimal workTimeByCubic = new BigDecimal(0);      //包方工时

    @Column
    private Long priceByTimer = 0L;     //包时单价

    @Column
    private Long amountByTimer = 0L;    //计时金额

    @Column
    private Long amountByCubic = 0L;    //包方金额

    @Column
    private Long amountByCount = 0L;    //总金额

    @Column
    private Long oilCount = 0L;     //用油量

    @Column
    private Long priceByOil = 0L;  //油单价

    @Column
    private Long amountByOil = 0L;  //用油金额

    @Column(columnDefinition = "DOUBLE")
    private BigDecimal oilConsumption = new BigDecimal(0);  //油耗

    @Column
    private Long rent = 0L;     //房租

    @Column
    private Long amountByMeals = 0L;    //伙食费

    @Column
    private Long subsidyAmount =0L;     //补贴

    @Column
    private Long balance = 0L;      //余额

    @Lob
    @Column(columnDefinition="text")
    private String remark = "";     //备注

    @Column
    private Date reportDate = null;     //统计日期

    @Column
    private String ownerName = "";      //业主名称

    private Boolean publishWx = false;      // 微信小程序是否可看报表
}
