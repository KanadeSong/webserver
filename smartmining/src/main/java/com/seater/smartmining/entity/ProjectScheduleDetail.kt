package com.seater.smartmining.entity

import com.seater.smartmining.enums.PricingTypeEnums
import com.seater.smartmining.utils.schedule.AutoScheduleType
import lombok.Data
import java.math.BigDecimal

import javax.persistence.*
import java.util.Date

@Entity
@Table(uniqueConstraints = arrayOf(UniqueConstraint(columnNames = arrayOf("projectId", "projectCarId"))))
data class ProjectScheduleDetail (

        @Id
        @GeneratedValue
        var id: Long = 0L,

        var diggingMachineCode :String = "",      //项目中的挖机编号

        var projectId: Long = 0L,    //参与的项目编号

        var projectDiggingMachineId: Long = 0L,    //参与的项目车编号

        var diggingMachineId: Long = 0L,    //车主选进来项目的车的id

        var createTime: Date = Date(),

        var updateTime: Date = Date(),

        var brandId: Long = 0L,      //品牌ID

        var brandName: String = "", //品牌名

        var modelId: Long = 0L,      //型号ID

        var modelName: String = "", //型号名

        var groupCode: String = "",      // 分组编号

        var placeId: Long = 0L,      //工作地点ID

        var placeName: String = "",      //工作地点名称

        var projectSlagSiteId: Long = 0L,      //渣场ID

        var projectSlagSiteName: String = "",      //渣场名称

        var projectCarId: Long = 0L,     //渣车ID

        var carId: Long = 0L,     //渣车ID

        var carCode: String = "",     //渣车编号

        var carUid: String = "",     //渣车uid

        var carBrandId: Long = 0L,      //渣车品牌ID

        var carBrandName: String = "", //渣车品牌名

        var carModelId: Long = 0L,      //渣车型号ID

        var carModelName: String = "", //渣车型号名

        var materialId: Long? = null,   //当前装载物料ID

        var materiaName: String? = null, //物料名称

        var distance: Long? = null,    //当前运距

        @Enumerated(EnumType.STRING)
        var pricingType: PricingTypeEnums = PricingTypeEnums.Unknow,     //计价方式

        var carOrderId: Long = 0L,      //调度指令ID

        var carOrderTime: Date = Date(0),   //调度时间

        @Enumerated(EnumType.STRING)
        var autoScheduleType: AutoScheduleType = AutoScheduleType.Unknow,    //状态

        var projectCarWorkInfoId: Long = 0L,      //工作信息ID

        @Enumerated(EnumType.STRING)
        var projectCarWorkInfoStatus: ProjectCarWorkStatus = ProjectCarWorkStatus.Unknown,      //工作状态

        var timeLoad: Date = Date(0), //装载时间

        var disable: Boolean = true,          //禁用

        var onlineIs: Boolean? = false,          //是否在线

        var offlineIs: Boolean? = false,          //是否离线

        @Enumerated(EnumType.STRING)
        var jobsts: FixJobStatus = FixJobStatus.Unknow,  //当前状态

        var realDigId: Long? = null,

        var realDigDistance: BigDecimal? = null,

        var realSiteIdList: String? = null,

        var realSiteDistanceList: String? = null,

        var changeTime: Date? = null,

        var workTimeNum: Int? = 0,

        var nearDig: Boolean? = false,

        var reAuto: Boolean? = false,

        var secondAuto: Boolean? = false,

        var nearDigTime: Date? = null,

        var leaveDigTime: Date? = null
)


enum class FixJobStatus(remark: String){
        Unknow("未知"),
        Loaded("装载离开"),
        Checked("满载检测"),
        SlagSite("渣场刷卡"),
        Ready("准备");
}