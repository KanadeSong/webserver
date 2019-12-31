package com.seater.smartmining.entity;

import com.seater.smartmining.enums.ProjectDeviceType;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/7/4 0004 10:27
 */
@Table
@Entity
@Data
public class WorkMergeErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;           //主键ID

    @Column
    private Long projectId = 0L;        //项目编号

    @Column
    private int lineNumber = 0;     //出错行数

    @Column
    private String message = "";        //异常信息

    @Column(columnDefinition = "text")
    private String detailMessage = "";      //异常详情

    @Column(columnDefinition = "text")
    private String params = "";     //请求参数

    @Column
    @Enumerated(EnumType.ORDINAL)
    private ProjectDeviceType projectDevice = ProjectDeviceType.Unknown;        //终端类型

    @Column
    private String eventId = "";        //事件ID

    @Column
    private Long pktID = 0L;            //包ID

    @Column
    private String uid = "";            //UID

    @Column
    private Long carId = 0L;        //车辆ID

    @Column
    private String carCode = "";        //车辆编号

    @Column
    private Date timeLoad = new Date(0L);       //装载时间

    @Column
    private Date timeCheck = new Date(0L);

    @Column
    private Date timeDischarge = new Date(0L);

    @Column
    private Date createDate = new Date(0L);
}
