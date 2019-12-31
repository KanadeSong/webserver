package com.seater.smartmining.entity;

import com.seater.smartmining.enums.ProjectDeviceType;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/12/16 0016 17:34
 */
@Entity
@Table
@Data
public class ProjectRunningTrajectoryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键ID

    @Column
    private Long projectId = 0L;        //项目ID

    @Column
    private String uid = "";        //终端UID

    @Column
    private ProjectDeviceType deviceType = ProjectDeviceType.Unknown;     //设备类型

    @Column
    private Date dateIdentification = new Date(0);      //日期标识

    @Column
    private Shift shift = Shift.Unknown;        //班次

    @Column(columnDefinition = "text")
    private String runningTrajectory = "";          //对应终端的定位信息
}
