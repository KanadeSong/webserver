package com.seater.smartmining.entity;

import com.seater.smartmining.enums.PricingTypeEnums;
import com.seater.smartmining.enums.WorkMergeErrorEnum;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/3 0003 13:48
 */
@Entity
@Table
@Data
public class ProjectMqttCardReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键ID

    @Column
    private Long projectId = 0L;       //项目ID

    @Column
    private Long carId = 0L;        //渣车ID

    @Column
    private String carCode = "";        //渣车编号

    @Column
    private Long loader = 0L;       //物料

    @Column
    private String loaderName = "";     //物料名称

    @Column
    private Long machineId = 0L;    //挖机ID

    @Column
    @Enumerated(EnumType.ORDINAL)
    private ProjectDispatchMode dispatchMode = ProjectDispatchMode.Unknown;        //调度模式

    @Column
    private Integer errorCode = 0;          //异常编号

    @Column
    private String errorCodeMessage = "";       //异常编号信息

    @Column
    private String message = "";        //异常信息

    @Column
    @Enumerated(EnumType.ORDINAL)
    private Shift shift = Shift.Unknown;        //班次

    @Column(columnDefinition = "text")
    private String remark = "";

    @Column
    private Date dateIdentification = new Date(0);

    @Column
    private Date timeDischarge = new Date(0);   //卸载时间

    @Column
    private Date timeLoad = new Date(0);        //装载时间

    @Column
    @Enumerated(EnumType.ORDINAL)
    private WorkMergeErrorEnum mergeError = WorkMergeErrorEnum.UNKNOW;

    @Column
    private Boolean uploadByDevice = true;

    @Lob
    @Column(columnDefinition = "text")
    private String locationText = "";

    @Column
    private String machineMayBe = "";

    @Column(columnDefinition = "text")
    private String exceptionDetails = "";

    @Column
    private Date creatTime = new Date();        //创建时间

    @Column
    private Date startTime = new Date(0);       //查询开始时间

    @Column
    private Date endTime = new Date(0);     //查询结束时间

    @Column(columnDefinition = "text")
    private String lastWork = "";       //上一次作业完成情况

    @Column
    private Long second = 0L;       //相差秒数

    @Column
    private Boolean detail = false;     //  是否处理

    @Column
    private Long slagSiteId = 0L;       //卸载渣场ID

    @Column
    private String slagSiteName = "";       //卸载渣场名称
}
