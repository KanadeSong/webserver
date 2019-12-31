package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/2/28 0028 14:51
 */
@Entity
@Table
@Data
public class ProjectDiggingPartCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键编号

    @Column
    private Long projectId = 0L;    //项目编号

    @Column
    private Long machineId = 0L;    //挖机主键编号

    @Column
    private String machineCode = "";    //挖机编号

    @Column
    private Long totalId = 0L;  //合计的主键编号

    @Column
    private Date faultTime = null;      // 故障时间

    @Column
    private BigDecimal workTimeByTimer = new BigDecimal(0);      //计时工作时间

    @Column
    private BigDecimal workTimeByCubic = new BigDecimal(0);      //包方工作时间

    /*@Column
    private Long totalCubicByTimer = 0L;        //包时总方量

    @Column
    private Long totalCubicByCubic = 0L;        //包方总方量

    @Column
    private Long totalCountByTimer = 0L;        //包时总车数

    @Column
    private Long totalCountByCubic = 0L;        //包方总车数*/

    @Column
    private Long priceByTimer = 0L;     //台时单价

    @Column
    private Long amountByTimer = 0L;    //台时金额

    @Column
    private Long amountByCubic = 0L;    //包方金额

    @Column
    private Long amountByCount = 0L;    //总合计

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
    private Date reportDate = null;     //统计时间

    @Column
    private Date createDate = null;     //创建时间
}
