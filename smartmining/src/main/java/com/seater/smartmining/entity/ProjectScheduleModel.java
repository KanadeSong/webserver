package com.seater.smartmining.entity;

import com.seater.smartmining.enums.DeviceStartStatusEnum;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:排班组方案模板
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/15 0015 10:19
 */
@Entity
@Table
@Data
public class ProjectScheduleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id = 0L;       //主键ID

    @Column
    public Long programmeId = 0L;

    @Column
    public String programmeName = "";

    @Column
    public String managerId = "";      //管理员ID

    @Column
    public String managerName = "";    //管理员名称

    @Column
    public Long createId = 0L;         //创建者ID

    @Column
    public String createName = "";     //创建者名称

    @Column
    public Long modifyId = 0L;        //修改者ID

    @Column
    public String modifyName = "";      //修改者名称

    @Column
    public Long projectId = 0L;        //项目ID

    @Column
    public String groupCode = "";      // 分组编号

    @Column
    public Date createTime = null;     //创建日期

    @Column
    public Date modifyTime = null;      //修改日期

    @Column
    public Long placeId = 0L;      //工作地点ID

    @Column
    public String placeName = "";      //工作地点名称

    @Column
    public String slagSiteId = "";

    @Column
    public String slagSiteName = "";

    @Column
    @Enumerated(EnumType.ORDINAL)
    public DeviceStartStatusEnum deviceStartStatus = DeviceStartStatusEnum.UnKnow;      //终端启动状态

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    public ProjectDispatchMode dispatchMode = ProjectDispatchMode.Unknown;           //调度模式

    @Column
    public String scheduleCode = "";            //用户手动输入的分组编号
}
