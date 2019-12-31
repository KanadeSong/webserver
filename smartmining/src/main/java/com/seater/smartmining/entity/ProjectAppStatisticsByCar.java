package com.seater.smartmining.entity;

import com.seater.smartmining.enums.ShiftsEnums;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/6/9 0009 11:34
 */
@Entity
@Table
@Data
public class ProjectAppStatisticsByCar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;           //主键ID

    @Column
    private Long projectId = 0L;        //项目ID

    @Column
    private String carCode = "";        //车辆编号

    @Column
    private Integer carCount = 0;       //车数

    @Column
    private Long cubic = 0L;        //装载方量

    @Column
    private ShiftsEnums shift = ShiftsEnums.UNKNOW;         //班次

    @Column
    private Date dateIdentification = null;     //日期标识

    @Column
    private Date createDate = null;         //创建时间
}
