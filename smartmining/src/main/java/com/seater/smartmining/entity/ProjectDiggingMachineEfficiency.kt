package com.seater.smartmining.entity

import com.seater.smartmining.enums.*

import javax.persistence.*
import java.io.Serializable
import java.math.BigDecimal
import java.util.Date

@Entity
data class ProjectDiggingMachineEfficiency (
    @Id
    @GeneratedValue
    var id: Long = 0L,

    var machineCode :String = "",      //项目中的挖机编号

    var projectId: Long = 0L,    //参与的项目编号

    var projectDiggingMachineId: Long = 0L,    //参与的项目车编号

    var diggingMachineId: Long = 0L,    //车主选进来项目的车的id

    var efficiency: BigDecimal? = null, //效率

    var timeListStr: String = "", //时间数组

    var defaultTime: Long? = null, //默认时间

    var createTime: Date = Date(),

    var updateTime: Date = Date(),

    var lastTime: Date? = null,

    var lastHourCarNum: Int = 0
)
