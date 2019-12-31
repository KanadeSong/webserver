package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/1 0001 17:23
 */
@Entity
@Table
@Data
public class ProjectCubicDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;               //主键编号

    @Column
    private Long projectId = 0L;        //项目编号

    @Column
    private Long totalId = 0L;      //合计编号

    @Column
    private Long carId = 0L;        //渣车主键编号

    @Column
    private String carCode = "";        //渣车编号

    @Column
    private Long capacity = 0L;             //渣车容量

    @Column
    private Long materialId = 0L;           //物料主键编号

    @Column
    private String materialName = "";       //物料名称

    @Column
    private Long cars = 0L;         //车数

    @Column
    private Long cubics = 0L;       //方量

    @Column
    private Long amountByShould = 0L;   //余额

    @Column
    private Date reportDate = null;

    @Column
    private Date createDate = null;

    @Column
    private Long machineId = 0L;

}
