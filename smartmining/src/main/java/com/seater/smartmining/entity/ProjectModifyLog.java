package com.seater.smartmining.entity;

import com.seater.smartmining.enums.ModifyEnum;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/7/16 0016 9:38
 */
@Entity
@Table
@Data
public class ProjectModifyLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键ID

    @Column(nullable = false)
    private Long projectId = 0L;        //项目ID

    @Column(nullable = false)
    private String projectName = "";        //项目名称

    @Column(nullable = false)
    private Date beforeEarlyStartTime = null;        //修改前早班开始时间

    @Column(nullable = false)
    private Date beforeEarlyEndTime = null;          //修改前早班结束时间

    @Column(nullable = false)
    private Date beforeNightStartTime = null;           //修改前晚班开始时间

    @Column(nullable = false)
    private Date beforeNightEndTime = null;             //修改前晚班结束时间

    @Column(nullable = false)
    private Long beforeOilPrice = 0L;       //修改前 油价

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProjectWorkTimePoint beforeEarlyEndPoint = ProjectWorkTimePoint.Today;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProjectWorkTimePoint beforeNightStartPoint = ProjectWorkTimePoint.Today;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProjectWorkTimePoint beforeNightEndPoint = ProjectWorkTimePoint.Tomorrow;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProjectType beforeProjectType = ProjectType.Unknown;      //修改前项目类型

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProjectDispatchMode beforeDispatchMode = ProjectDispatchMode.Unknown;           //修改前调度模式

    @Column
    private ProjectStatus beforeStatus = ProjectStatus.Unknown; //修改前状态

    @Column
    private Date earlyStartTime = null;         //修改后早班开始时间

    @Column
    private Date earlyEndTime = null;           //修改后早班结束时间

    @Column
    private Date nightStartTime = null;         //修改后晚班开始时间

    @Column
    private Date nightEndTime = null;           //修改后晚班结束时间

    @Column
    private Long oilPrice = 0L;         //修改后油价

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProjectWorkTimePoint earlyEndPoint = ProjectWorkTimePoint.Today;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProjectWorkTimePoint nightStartPoint = ProjectWorkTimePoint.Today;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProjectWorkTimePoint nightEndPoint = ProjectWorkTimePoint.Tomorrow;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProjectType projectType = ProjectType.Unknown;      //修改后项目类型

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProjectDispatchMode dispatchMode = ProjectDispatchMode.Unknown;           //修改后调度模式

    @Column
    private ProjectStatus status = ProjectStatus.Unknown; //修改后状态

    @Column
    private Date createTime = null;         //创建时间

    @Column(nullable = false)
    private Long userId = 0L;           //修改人ID

    @Column(nullable = false)
    private String userName = "";           //修改人名称

    @Column
    @Enumerated(EnumType.ORDINAL)
    private ModifyEnum modifyEnum = ModifyEnum.Unknow;

    @Column(nullable = false)
    private Boolean notEnd = false;          //是否结束
}
