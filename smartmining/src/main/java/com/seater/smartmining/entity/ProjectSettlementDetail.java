package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/2 0002 12:40
 */
@Entity
@Table
@Data
public class ProjectSettlementDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键编号

    @Column
    private Long projectId = 0L;    //项目编号

    @Column
    private Long carId = 0L;    //渣车编号

    @Column
    private Long totalId = 0L;  //合计的主键编号

    @Column
    private Long distance = 0L;     //运距

    @Column
    private Long materialId = 0L;     //物料编号

    @Column
    private String materialName = "";       //物料名称

    @Column
    private Integer carsCount = 0;    //车数

    @Column
    private Long cubicCount = 0L;   //方量

    @Column
    private Long price = 0L;    //物料单价

    @Column
    private Long amount = 0L;   //金额

    @Column
    private Date reportDate = null;     //统计日期

    private Date createDate = null;     //创建时间
}
