package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/4 0004 20:57
 */
@Entity
@Table
@Data
public class ProjectMachineLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long projectId = 0L;

    @Column(columnDefinition = "text")
    private String diggingMachineText = "";

    @Column
    private Long carId = 0L;

    @Column
    private String carCode = "";

    @Column(scale = 6)
    private BigDecimal longitudeByCar = BigDecimal.ZERO;     //渣车经度

    @Column(scale = 6)
    private BigDecimal latitudeByCar = BigDecimal.ZERO;      //渣车纬度

    @Column
    @Enumerated(EnumType.ORDINAL)
    private  ProjectDispatchMode dispatchMode = ProjectDispatchMode.Unknown;        //调度模式

    @Column
    @Enumerated(EnumType.ORDINAL)
    private Shift shift = Shift.Unknown;

    @Column
    private Date dateIdentification = new Date(0);      //日期标识

    @Column
    private String scheduleMachineId = "";      //对应排班挖机ID

    @Column
    private String scheduleMachieCode = "";     //对应排班挖机编号

    @Column
    private String scheduleMachineCodeByDevice = "";        //终端上传对应的排班挖机编号

    @Column
    private Date createTime = new Date();       //创建时间

    @Column(columnDefinition = "text")
    private String scheduleText = "";
}
