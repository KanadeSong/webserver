package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:车辆效率表
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/12/17 0017 15:53
 */
@Entity
@Table
@Data
public class ProjectCarEfficiency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键ID

    @Column
    private Long projectId = 0L;        //项目ID

    @Column
    private Long carId = 0L;        //车辆ID

    @Column
    private String carCode = "";        //车辆编号

    @Column
    private CarType carType = CarType.Unknow;       //车辆类型

    @Column
    private Long workTime = 0L;         //工作时长

    @Column
    private Long carCount = 0L;         //总车数

    @Column
    private BigDecimal efficiency = BigDecimal.ZERO;        //效率

    @Column
    private Shift shift = Shift.Unknown;        //班次

    @Column
    private Date dateIdentification = null;     //日期标识

    @Column
    private Date createTime = new Date();       //创建时间
}
