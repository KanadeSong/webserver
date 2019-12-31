package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/2 0002 15:30
 */
@Entity
@Table
@Data
public class ProjectSettlementSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键编号

    @Column
    private Long carId = 0L;    //渣车编号

    @Column
    private Long detailId = 0L;     //详情主键编号

    @Column
    private Long projectId = 0L;    //项目编号

    @Column
    private Long totalId = 0L;  //合计编号

    @Column
    private Long carsCount = 0L;    //车数

    @Column
    private Long oilCount = 0L;   //油量

    @Column
    private Long amountByOil = 0L;  //用油金额

    @Column
    private Long price = 0L;    //单价

    @Column
    private Long rent = 0L;     //房租

    @Column
    private Long amountByElse = 0L;     //合计其他费用

    @Column
    private Long amountByMeals = 0L;    //伙食费

    @Column
    private Long subsidyAmount =0L;     //补贴

    @Column
    private Long balance = 0L;      //余额

    @Column
    private Date reportDate = null;     //统计日期

    @Column
    private Date createDate = null;     //创建日期


}
