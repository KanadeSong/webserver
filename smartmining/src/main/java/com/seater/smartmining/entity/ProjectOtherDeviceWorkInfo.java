package com.seater.smartmining.entity;

import com.seater.smartmining.enums.ProjectOtherDeviceStatusEnum;
import com.seater.smartmining.enums.ShiftsEnums;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:其他设备工作信息 穿孔机除外
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/10 0010 15:22
 */
@Entity
@Table
@Data
public class ProjectOtherDeviceWorkInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键ID

    @Column
    private Long projectId = 0L;        //项目ID

    @Column
    private Long deviceId = 0L;     //车辆ID

    @Column
    private String code = "";       //车辆编号

    @Column
    @Enumerated(EnumType.ORDINAL)
    private ShiftsEnums shift = ShiftsEnums.UNKNOW;        //班次

    @Column
    private Date startTime = null;          //开始时间

    @Column
    private Date endTime = null;            //结束时间

    @Column
    @Enumerated(EnumType.ORDINAL)
    private ProjectOtherDeviceStatusEnum status = ProjectOtherDeviceStatusEnum.Unknow;      //状态

    @Column
    private CarType carType = CarType.Unknow;       //车辆类型

    @Column
    private BigDecimal workTime = BigDecimal.ZERO;      //工作时长

    @Column
    private BigDecimal amount = BigDecimal.ZERO;        //工作金额

    @Column
    private Long auditorId = 0L;        //审核人ID

    @Column
    private String auditorName = "";        //审核人名称

    @Column(columnDefinition = "text")
    private String remark = "";         //备注

    @Column
    private Date createTime = null;     //创建时间

    @Column
    private Date dateIdentification = null;     //日期标识
}
