package com.seater.user.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;

/**
 *  smartmining 模块如果修改了 Project 类，必须先把这个类更新了,两个类字段更新就行
 *  /@Entity(name = "project")/ 不能删,因为需要指向同一张数据表
 * @Date 2019年3月23日 11点35分
 * @Author xueqichang
 * @Email 1369521908@qq.com
 */
@Data
@Entity(name = "project")
@NoArgsConstructor
public class UProject implements Serializable {

    /**
     * 序列化id
     */
    private static final long serialVersionUID = 4125096758372084309L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column(nullable = false)
    private String name = "";                                       //项目名称

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProjectType projectType = ProjectType.Unknown;      //项目类型

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    ProjectDispatchMode dispatchMode = ProjectDispatchMode.Unknown;           //调度模式

    @Column
    private Date startTime = null;          //开始时间

    @Column
    private Date endTime = null;            //结束时间

    @Column(nullable = false)
    private Boolean isEnd = false;          //是否结束

    @Column(nullable = false)
    private Long oilPirce = 0L;           //油价(分/升)

    @Column(nullable = false)
    private Date earlyStartTime = new Time(-7200000L);   //早班开始时间

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProjectWorkTimePoint earlyEndPoint = ProjectWorkTimePoint.Today;

    @Column(nullable = false)
    private Date earlyEndTime = new Time(35999000L);   //早班结束时间

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProjectWorkTimePoint nightStartPoint = ProjectWorkTimePoint.Today;

    @Column(nullable = false)
    private Date nightStartTime = new Time(36000000);   //晚班开始时间

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProjectWorkTimePoint nightEndPoint = ProjectWorkTimePoint.Tomorrow;

    @Column(nullable = false)
    private Date nightEndTime = new Time(-7201000);   //晚班结束时间

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProjectStatus status = ProjectStatus.Unknown; //状态

    @Column(nullable = false)
    private Date addTime = new Date();      //添加时间

    @Column
    private String avatar;      //  项目图标

    @Column(scale = 6)
    private BigDecimal longitude = BigDecimal.ZERO;     //经度

    @Column(scale = 6)
    private BigDecimal latitude = BigDecimal.ZERO;      //纬度

}



