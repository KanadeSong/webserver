package com.seater.smartmining.entity;

import com.seater.smartmining.enums.ModifyEnum;
import com.seater.smartmining.enums.SlagSiteEnum;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/8/15 17:43
 */

@Entity
@Data
public class ProjectSlagSiteModifyLog implements Serializable {

    private static final long serialVersionUID = -4839259801634171982L;

    ///////////////////////////////////////////
    //修改前

    private Long modifyId = 0L;     //渣场id主键

    @Column
    private Long beforeProjectId = null;    //参与的项目编号

    @Column(nullable = false)
    private String beforeName = "";   //场地名称

    @Column
    private String beforeDescription = "";    //场地説明

    @Column(nullable = false)
    private Long beforeDistance = 0L;   //距离

    @Column(nullable = false)
    private Long beforeDeviceId = 0L; //设备ID

    @Column(nullable = false)
    private String beforeDeviceCode = ""; //设备编号

    @Column(nullable = false)
    private String beforeDeviceUid = "";              //设备UID

    @Column(nullable = false)
    private Long beforeSwipeIntervent = 300000L;      //刷卡间格

    @Column
    private SlagSiteEnum beforeSlagSite = SlagSiteEnum.UNKNOW;

    @Column
    private String beforeManagerId = "";

    @Column
    private String beforeManagerName = "";

    @Column
    private String beforeMaterialId = "";   //物料ID

    @Column
    private String beforeMaterialName = "";       //物料名称


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long projectId = null;    //参与的项目编号

    @Column(nullable = false)
    private String name = "";   //场地名称

    @Column
    private String description = "";    //场地説明

    @Column(nullable = false)
    private Long distance = 0L;   //距离

    @Column(nullable = false)
    private Long deviceId = 0L; //设备ID

    @Column(nullable = false)
    private String deviceCode = ""; //设备编号

    @Column(nullable = false)
    private String deviceUid = "";              //设备UID

    @Column(nullable = false)
    private Long swipeIntervent = 300000L;      //刷卡间格

    @Column
    private SlagSiteEnum slagSite = SlagSiteEnum.UNKNOW;

    @Column
    private String managerId = "";

    @Column
    private String managerName = "";

    @Column
    private Long materialId = 0L;   //物料ID

    @Column
    private String materialName = "";       //物料名称


    ////////////////////////////////////////////////



    @Column
    private Date createTime = null;         //创建时间

    @Column(nullable = false)
    private Long userId = 0L;           //修改人ID

    @Column(nullable = false)
    private String userName = "";           //修改人名称

    @Column
    @Enumerated(EnumType.ORDINAL)
    private ModifyEnum modifyEnum = ModifyEnum.Unknow;

}
