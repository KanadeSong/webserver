package com.seater.smartmining.entity;

import com.seater.smartmining.enums.ModifyEnum;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/15 0015 13:13
 */
@Entity
@Table
@Data
public class ProjectModifyScheduleModelLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键ID

    @Column
    private String beforeManagerId = "";      //管理员ID

    @Column
    private String beforeManagerName = "";    //管理员名称

    @Column
    private Long modifyId = 0L;         //创建者ID

    @Column
    private String modifyName = "";     //创建者名称

    @Column
    private Long projectId = 0L;        //项目ID

    @Column
    private String beforeGroupCode = "";      // 分组编号

    @Column
    private Date modifyTime = null;     //创建日期

    @Column
    private Long beforePlaceId = 0L;      //工作地点ID

    @Column
    private String beforePlaceName = "";      //工作地点名称

    @Lob
    @Column(columnDefinition="text")
    private String beforeMachineJson = "";        //修改前挖机json

    @Lob
    @Column(columnDefinition="text")
    private String beforeCarJson = "";            //修改前渣车json

    @Lob
    @Column(columnDefinition = "text")
    private String beforeScheduleJson = "";         //修改前分组信息

    @Column
    private String managerId = "";      //修改后管理员ID

    @Column
    private String managerName = "";    //修改后管理员名称

    @Column
    private String groupCode = "";      // 修改后分组编号

    @Column
    private Long placeId = 0L;      //修改后工作地点ID

    @Column
    private String placeName = "";      //修改后工作地点名称

    @Lob
    @Column(columnDefinition="text")
    private String machineJson = "";        //修改后挖机json

    @Lob
    @Column(columnDefinition="text")
    private String carJson = "";            //修改后渣车json

    @Lob
    @Column(columnDefinition = "text")
    private String scheduleJson = "";       //修改后排班信息

    @Column
    @Enumerated(EnumType.ORDINAL)
    private ModifyEnum modifyEnum = ModifyEnum.Unknow;
}
