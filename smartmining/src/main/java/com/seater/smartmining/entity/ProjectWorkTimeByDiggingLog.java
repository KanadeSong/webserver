package com.seater.smartmining.entity;

import com.seater.smartmining.enums.DeviceDoStatusEnum;
import com.seater.smartmining.enums.DeviceStartStatusEnum;
import com.seater.smartmining.enums.ProjectDeviceType;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/23 0023 15:44
 */
@Entity
@Table
@Data
public class ProjectWorkTimeByDiggingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键ID

    @Column
    private Long projectId = 0L;        //项目ID

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProjectDeviceType projectDeviceType;        //终端类型

    @Column
    private Long carId = 0L;        //车辆ID

    @Column
    private String carCode = "";        //车辆编号

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private DeviceDoStatusEnum doStatus = DeviceDoStatusEnum.UnKnow;        //请求状态

    @Column
    private Long createId = 0L;     //操作人ID

    @Column
    private String createName = "";     //操作人名称

    @Column
    private Date dateIdentification = null;     //日期标识

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Shift shift = Shift.Unknown;        //班次

    @Column
    private Date createTime = new Date();       //创建时间

    @Column
    private Boolean success = false;        //是否请求成功

    @Column(columnDefinition = "text")
    private String remark = "";     //备注

    @Column
    private String uid = "";        //终端uid

    @Column
    private ProjectDeviceStatus deviceStatus = ProjectDeviceStatus.Unknown;     //终端在线状态
}
