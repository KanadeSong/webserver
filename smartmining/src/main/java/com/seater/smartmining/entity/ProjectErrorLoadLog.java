package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:渣车未到指定挖机装载的记录表
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/1 0001 0:05
 */
@Entity
@Table
@Data
public class ProjectErrorLoadLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键ID

    @Column
    private Long projectId = 0L;        //项目ID

    @Column
    private Long carId = 0L;        //渣车ID

    @Column
    private String carCode = "";        //渣车编号

    @Column
    private Date timeDischarge = null;      //卸载时间

    @Column
    private String machineId = "";      //应该去装载的挖机ID数组

    @Column
    private String machineCode = "";        //应该去装载挖机编号数组

    @Column
    private Shift shift = Shift.Unknown;        //班次

    @Column
    private Integer count = 0;      // 车shu

    @Column
    private Date dateIdentification = null;     //日期标识

    @Column
    private Date modifyTime = null;     //修改时间

    @Column
    private Date createTime = null;         //创建时间
}
