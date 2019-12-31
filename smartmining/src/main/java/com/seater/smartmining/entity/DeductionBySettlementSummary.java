package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/20 0020 9:56
 */
@Entity
@Table
@Data
public class DeductionBySettlementSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键ID

    @Column
    private Long carId = 0L;        //车辆ID

    /*@Column
    private Long summaryId = 0L; */       //账目结算明细的ID

    @Column
    private Long carsCount = 0L;        //总车数

    @Column
    private Long projectId = 0L;        //项目ID

    @Column
    private Long shouldPay = 0L;        //余额

    @Column
    private Long rent = 0L;         //房租

    @Column
    private Long amountByMeal = 0L;     //伙食费

    @Column
    private Long amountByElse = 0L;         //其他费用

    @Column
    private Long amountBySubsidyAmount = 0L;        //补贴

    @Column
    private Long thiryFive = 0L;            //35/车

    @Column
    private Date reportDate = null;     //统计日期

    @Column
    private Date createDate = null;     //创建日期
}
