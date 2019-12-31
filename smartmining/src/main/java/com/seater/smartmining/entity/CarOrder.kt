package com.seater.smartmining.entity

import com.seater.smartmining.enums.ProjectCarStatus
import com.seater.smartmining.service.impl.SendType
import com.seater.smartmining.utils.schedule.AutoScheduleType
import java.math.BigDecimal
import java.util.*
import javax.persistence.*


@Entity
@Table(uniqueConstraints = arrayOf(UniqueConstraint(columnNames = arrayOf("carId", "orderTime"))))
data class CarOrder(
        @Id
        @GeneratedValue
        var id: Long = 0,

        @Enumerated(EnumType.STRING)
        @Column(columnDefinition = "varchar(30) COMMENT'类型'")
        var carOrderType: CarOrderType = CarOrderType.Unknow,

        @Enumerated(EnumType.STRING)
        @Column(columnDefinition = "varchar(30) COMMENT'真实类型'")
        var carOrderRealType: CarOrderRealType? = CarOrderRealType.Unknow,

        @Enumerated(EnumType.STRING)
        @Column(columnDefinition = "varchar(30) COMMENT'状态'")
        var carOrderState: CarOrderState = CarOrderState.Ready,

        @Column(columnDefinition = "bigint(20) COMMENT'渣车id'")
        var carId: Long = 0L,

        @Column(columnDefinition = "varchar(255) COMMENT'渣车编号'")
        var carCode: String = "",

        @Column(columnDefinition = "varchar(255) COMMENT'渣车uid'")
        var carUid: String = "",

        @Enumerated(EnumType.STRING)
        @Column(columnDefinition = "varchar(255) COMMENT'渣车状态'")
        var carStatus: ProjectCarStatus = ProjectCarStatus.Unknow,

        @Column(columnDefinition = "bigint(20) COMMENT'来源id'")
        var fromUserId: Long? = null,

        @Column(columnDefinition = "bigint(20) COMMENT'项目id'")
        var projectId: Long? = null,

        @Column(columnDefinition = "varchar(255) COMMENT'来源'")
        var fromUserName: String? = null,

        @Column(columnDefinition = "datetime COMMENT'创建时间'")
        var createTime: Date = Date(),

        @Column(columnDefinition = "datetime COMMENT'下单时间'")
        var orderTime: Date? = null,

        @Column(columnDefinition = "datetime COMMENT'更新时间'")
        var updateTime: Date = Date(),

        @Column(columnDefinition = "bigint(20) COMMENT'挖机id'")
        var diggingMachineId: Long? = null,

        @Column(columnDefinition = "varchar(255) COMMENT'挖机编号'")
        var diggingMachineCode: String? = null,

        @Column(columnDefinition = "bigint(20) COMMENT'渣场id'")
        var slagSiteId: Long? = null,

        @Column(columnDefinition = "varchar(255) COMMENT'渣场名称'")
        var slagSiteName: String? = null,

        @Column(columnDefinition = "text COMMENT'辅助任务内容'")
        var message: String? = null,

        @Column(columnDefinition = "bigint(20) COMMENT'rid'")
        var rid: Long? = null,

        @Column(columnDefinition = "datetime COMMENT'发送时间'")
        var sendTime: Date? = null,

        @Column(columnDefinition = "datetime COMMENT'发送时间'")
        var firstSendTime: Date? = null,

        @Column(columnDefinition = "bit(1) COMMENT'结果'")
        var success: Boolean? = null,

        @Column(columnDefinition = "bigint(20) COMMENT'序号'")
        var orderNumber: Long? = null,

        @Column(columnDefinition = "bigint(20) COMMENT'发送次数'")
        var sendNum: Long = 0L,

        @Column(columnDefinition = "varchar(255) COMMENT'备注'")
        var remark: String? = null,

        @Column(columnDefinition = "datetime COMMENT'修改时间'")
        var detailUpdateTime: Date = Date(),

        @Enumerated(EnumType.STRING)
        @Column(columnDefinition = "varchar(255) COMMENT'发送方式'")
        var sendType: SendType = SendType.request,

        @Enumerated(EnumType.STRING)
        @Column(columnDefinition = "varchar(255) COMMENT'挖机分配方式'")
        var digOrderType: AutoOrderType = AutoOrderType.Unknow,

        @Enumerated(EnumType.STRING)
        @Column(columnDefinition = "varchar(255) COMMENT'渣场分配方式'")
        var siteOrderType: AutoOrderType = AutoOrderType.Unknow,

        @Enumerated(EnumType.STRING)
        @Column(columnDefinition = "varchar(255) COMMENT'AutoScheduleType'")
        var autoScheduleType: AutoScheduleType = AutoScheduleType.Unknow,

        @Column(columnDefinition = "datetime COMMENT'装载时间'")
        var loadTime: Date? = null,

        @Column(columnDefinition = "bit(1) COMMENT'是否固定挖机'")
        var fixDig: Boolean? = false,

        @Column(columnDefinition = "bit(1) COMMENT'是否固定渣场'")
        var fixSite: Boolean? = false,

        @Enumerated(EnumType.STRING)
        @Column(columnDefinition = "varchar(255) COMMENT'渣车工作状态'")
        var carJobStatus: FixJobStatus = FixJobStatus.Unknow,

        @Column(columnDefinition = "bigint(20) COMMENT'上传的挖机id'")
        var realDigId: Long? = null,

        var realDigDistance: BigDecimal? = null,

        @Column(columnDefinition = "varchar(255) COMMENT'上传的渣场id列表'")
        var realSiteIdList: String? = null,

        @Column(columnDefinition = "varchar(255) COMMENT'上传的渣场距离列表'")
        var realSiteDistanceList: String? = null,

        @Column(columnDefinition = "varchar(255) COMMENT'分组编号'")
        var groupCode: String? = null,

        @Column(columnDefinition = "bit(1) COMMENT'是否重新分配'")
        var reAuto: Boolean? = false,

        @Column(columnDefinition = "bit(1) COMMENT'是否二次分配'")
        var secondAuto: Boolean? = false,

        @Column(columnDefinition = "bit(1) COMMENT'是否取消'")
        var cancel: Boolean? = false,

        @Column(columnDefinition = "bigint(20) COMMENT'重用次数'")
        var reUseNum: Long? = null,

        @Column(columnDefinition = "datetime COMMENT'重新分配时间'")
        var reAutoTime: Date? = null,

        @Column(columnDefinition = "datetime COMMENT'二次分配时间'")
        var secondAutoTime: Date? = null
)

enum class CarOrderType(val remark: String, val order: Int) {
    Unknow("未知", 0),
    Text("辅助", 1),
    Auto("智能", 2),
    Fix("固定", 3),
    Temp("临时", 4);
}

enum class CarOrderRealType(val remark: String, val order: Int) {
    Unknow("未知", 0),
    Text("辅助", 1),
    Auto("智能", 2),
    Fix("固定", 3),
    Temp("临时", 4);
}

enum class AutoOrderType(val remark: String, val order: Int) {
    Unknow("未知", 0),
    Auto("智能", 1),
    From("继承", 2),
    Rand("随机", 3);
}

enum class CarOrderState(val remark: String, val order: Int) {
    Ready("准备", 0),
    Send("发送", 1),
    Receive("接收", 2),
    End("结束", 3);
}

var autoSendFlag = ""