package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/12/2 0002 14:35
 */
@Entity
@Table
@Data
public class ProjectCarCountLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;           //主键ID

    @Column
    private Long projectId = 0L;        //项目ID

    @Column
    private Long carId = 0L;        //车辆ID

    @Column
    private String carCode = "";        //车辆编号

    @Column
    private CarType carType = CarType.Unknow;       //车辆类型

    @Column
    private Shift shift = Shift.Unknown;        //班次

    @Column
    private Date dateIdentification = null;     //日期标识

    @Column
    private Long materialId = 0L;       //物料ID

    @Column
    private String materialName = "";       //物料名称

    @Column
    private Date timeDischarge = null;      //卸载时间

    @Column
    private Long distance = 0L;     //运距

    @Column
    private Date createTime = new Date();       //创建时间
}
