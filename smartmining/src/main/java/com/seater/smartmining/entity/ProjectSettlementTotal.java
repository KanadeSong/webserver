package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/2 0002 16:29
 */
@Entity
@Table
@Data
public class ProjectSettlementTotal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键编号

    @Column
    private Long projectId = 0L;    //项目编号

    @Column
    private Long carId = 0L;        //车辆编号

    @Lob
    @Column(columnDefinition="text")
    private String totalJson = "";      //合计的Json字符串

    @Column
    private Long carsCount = 0L;        //合计总车数

    @Column
    private Long oilCount = 0L;         //合计油量

    @Column
    private Long amountByOil = 0L;  //用油金额

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
    private Long mileage = 0L;  //里程数

    @Column
    private String ownerName = "";  //业主名称

    @Column
    private Long capacity = 0L;     //容量

    @Column
    private Date reportDate = null;         //统计日期

    private Boolean publishWx = false;      // 微信小程序是否可看报表
}
