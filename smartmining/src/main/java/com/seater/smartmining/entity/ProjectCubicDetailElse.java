package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/5 0005 9:07
 */
@Entity
@Table
@Data
public class  ProjectCubicDetailElse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long projectId = 0L;

    @Column
    private Long totalId = 0L;

    @Column
    private Long detailId = 0L;

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
    private Date reportDate = null;

    @Column
    private Date createDate = null;

    @Column
    private Long machineId = 0L;
}
