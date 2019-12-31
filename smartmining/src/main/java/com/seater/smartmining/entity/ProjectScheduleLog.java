package com.seater.smartmining.entity;

import com.seater.smartmining.enums.ScheduleEnum;
import com.seater.smartmining.enums.ShiftsEnums;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:任务调度执行日志
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/7/23 0023 9:46
 */
@Entity
@Table
@Data
public class ProjectScheduleLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //主键ID
    private Long id = 0L;

    @Column
    private Long projectId = 0L;        //项目ID

    @Column
    private Date createDate = null;         //创建时间

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ScheduleEnum scheduleEnum = ScheduleEnum.Unknow;        //任务调度执行类型

    @Column
    private Date dateIdentification = null;     //日期标识

    @Column
    private String cron = "";           //任务调度的时间
}
