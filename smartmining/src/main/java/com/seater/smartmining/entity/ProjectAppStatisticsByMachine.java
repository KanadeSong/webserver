package com.seater.smartmining.entity;

import com.seater.smartmining.enums.ShiftsEnums;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/6/9 0009 11:36
 */
@Entity
@Table
@Data
public class ProjectAppStatisticsByMachine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long projectId = 0L;        //项目ID

    @Column
    private String machineCode = "";        //挖机编号

    @Column
    private Long workTime = 0L;     //工作时间

    @Column
    private ShiftsEnums shifts = ShiftsEnums.UNKNOW;        //班次

    @Column
    private Date dateIdentification = null;     //日期标识

    @Column
    private Date createDate = null;         //创建时间

}
