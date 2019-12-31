package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/1 0001 17:42
 */
@Entity
@Table
@Data
public class ProjectCubicDetailTotal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;               //主键编号

    @Column
    private Long machineId = 0L;    //挖机编号

    @Column
    private Long projectId = 0L;        //项目编号

    @Column(columnDefinition="text")
    private String totalJson = "";      //合计字符串

    @Column
    private Long carsByTemp = 0L;   //临时车数

    @Column
    private Long cubicByTemp = 0L;  //临时方量

    @Column
    private Long carsByTotal = 0L;  //合计车数

    @Column
    private Long cubicByTotal = 0L;     //合计方量

    @Column
    private Long price = 0L;    //单价

    @Column
    private Long amount = 0L;   //金额

    @Column
    private Long oilCount = 0L; //油量

    @Column
    private Long amountByOil = 0L;  //加油金额

    @Column
    private Long amountByShould = 0L;   //余额

    @Column
    private Date reportDate = null;

    @Column
    private String ownerName = "";  //业主名称
}
