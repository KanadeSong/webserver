package com.seater.smartmining.utils.schedule

import com.seater.smartmining.entity.ProjectScheduleDetail
import com.systech.helpers.toJsonString
import java.math.BigDecimal
import java.util.*



var defaultDiggingMachineIntervalLong: Long = 1000*60*2L //默认挖机分配间隔时长/ms
var defaultSlagSiteIntervalLong: Long = 1000*60*2L //默认渣场分配间隔时长/ms
var defaultDiggingMachineDistance: Long = 100L //默认挖机距离/米
var defaultSlagSiteDistance: Long = 1000L //默认渣场距离/米
var defaultWaitTimeLong: Long = 1000*60*5L //默认等待排班时间


fun autoScheduleForDiggingMachine(autoScheduleInfo: AutoScheduleInfo): List<ProjectCarScheduleInfo> {
    //println("--------------------autoScheduleForDiggingMachine----------autoScheduleInfo-------${autoScheduleInfo}")
    val projectDiggingMachineScheduleInfoList = autoScheduleInfo.projectDiggingMachineScheduleInfoList.filter { !it.groupCode.isNullOrBlank() && it.projectDiggingMachineId > 0L }
    val workScheduleInfo = autoScheduleInfo.workScheduleInfo
    val projectSlagSiteScheduleInfoList = autoScheduleInfo.projectSlagSiteScheduleInfoList.filter { it.slagSiteId > 0L }
    val projectDiggingMachineIdList = projectDiggingMachineScheduleInfoList.map { it.projectDiggingMachineId }.toSet()
    val projectSlagSiteIdList = projectSlagSiteScheduleInfoList.map { it.slagSiteId }.toSet()
    val projectCarScheduleInfoList = autoScheduleInfo.projectCarScheduleInfoList.filter { !it.groupCode.isNullOrBlank() && it.projectCarId > 0L &&
            when(it.autoScheduleType){
                AutoScheduleType.DiggingMachine -> it.projectDiggingMachineId > 0L && projectDiggingMachineIdList.contains(it.projectDiggingMachineId)
                AutoScheduleType.SlagSite -> it.slagSiteId > 0L && projectSlagSiteIdList.contains(it.slagSiteId)
                else -> true
            }
    }
    val projectCarScheduleGroupCodeList = projectCarScheduleInfoList.map { it.groupCode }.toSet()
    val projectDiggingMachineGroupCodeList = projectDiggingMachineScheduleInfoList.map { it.groupCode }.toSet()
    val groupCode = workScheduleInfo.groupCode
    if(groupCode.isNullOrBlank() || projectCarScheduleGroupCodeList.isEmpty() || projectDiggingMachineGroupCodeList.isEmpty()) throw Exception("分组信息不完整")
    if(groupCode != (projectCarScheduleGroupCodeList.firstOrNull()?: "") || groupCode != (projectDiggingMachineGroupCodeList.firstOrNull()?: "")) throw Exception("分组编号不一致")

    projectDiggingMachineScheduleInfoList.forEach {
        val digId = it.projectDiggingMachineId
        val carList = projectCarScheduleInfoList.filter { it.projectDiggingMachineId == digId && it.autoScheduleType == AutoScheduleType.DiggingMachine }
        //it.waitNum = carList.size
        //if(it.firstScheduleTime == null) it.firstScheduleTime = carList.minBy { it.diggingMachineTime }!!.diggingMachineTime
    }

    val date = Date()
    val dateTime = date.time
    val waitForScheduleCarList = projectCarScheduleInfoList.filter { it.autoScheduleType == AutoScheduleType.WaitForDiggingMachineSchedule }.toMutableList()
    val waitForScheduleCarListAllSize = waitForScheduleCarList.size
    val rt = mutableListOf<ProjectCarScheduleInfo>()
    projectDiggingMachineScheduleInfoList.filter { (dateTime - it.lastScheduleTime.time) >= it.intervalTimeLong }.sortedBy { it.waitNum }.forEach {
        waitForScheduleCarList.firstOrNull()?.apply {
            rt.add(this.apply {
                this.autoScheduleType = AutoScheduleType.DiggingMachine
                this.diggingMachineTime = Date()
                this.projectDiggingMachineId = it.apply {
                    this.lastScheduleTime = Date(dateTime + waitForScheduleCarListAllSize - waitForScheduleCarList.size)
                    //this.waitNum ++
                    //if(this.firstScheduleTime == null) this.firstScheduleTime = Date()
                }.projectDiggingMachineId
            })
            waitForScheduleCarList.remove(this)
        }
    }
    if(dateTime - workScheduleInfo.startTime.time >= workScheduleInfo.waitTimeLong){
        while(waitForScheduleCarList.firstOrNull() != null) {
            val it = waitForScheduleCarList.firstOrNull()!!
            rt.add(it.apply {
                val tDate = Date(dateTime + waitForScheduleCarListAllSize - waitForScheduleCarList.size)
                this.autoScheduleType = AutoScheduleType.DiggingMachine
                this.diggingMachineTime = tDate
                this.projectDiggingMachineId = projectDiggingMachineScheduleInfoList.filter {
                    val min = projectDiggingMachineScheduleInfoList.minBy {it.waitNum }
                    it.waitNum == min!!.waitNum
                }.minBy { (it.firstScheduleTime?: Date(0)) }!!.apply {
                    this.lastScheduleTime = tDate
                    //this.waitNum ++
                    //if(this.firstScheduleTime == null) this.firstScheduleTime = Date()
                }.projectDiggingMachineId
            })
            waitForScheduleCarList.remove(it)
        }
    }
    else 0 //Thread.sleep(10L)

    rt.addAll(waitForScheduleCarList)
    return rt.toList()
}

fun autoScheduleForSlagSite(autoScheduleInfo: AutoScheduleInfo): List<ProjectCarScheduleInfo> {
    //println("--------------------autoScheduleForSlagSite-----------------${autoScheduleInfo}")
    val projectDiggingMachineScheduleInfoList = autoScheduleInfo.projectDiggingMachineScheduleInfoList.filter { !it.groupCode.isNullOrBlank() && it.projectDiggingMachineId > 0L }
    val projectSlagSiteScheduleInfoList = autoScheduleInfo.projectSlagSiteScheduleInfoList.filter { it.slagSiteId > 0L }
    val projectDiggingMachineIdList = projectDiggingMachineScheduleInfoList.map { it.projectDiggingMachineId }.toSet()
    val projectSlagSiteIdList = projectSlagSiteScheduleInfoList.map { it.slagSiteId }.toSet()
    val projectCarScheduleInfoList = autoScheduleInfo.projectCarScheduleInfoList.filter { !it.groupCode.isNullOrBlank() && it.projectCarId > 0L &&
            when(it.autoScheduleType){
                AutoScheduleType.DiggingMachine -> it.projectDiggingMachineId > 0L && projectDiggingMachineIdList.contains(it.projectDiggingMachineId)
                AutoScheduleType.SlagSite -> it.slagSiteId > 0L && projectSlagSiteIdList.contains(it.slagSiteId)
                else -> true
            }
    }
    val date = Date()
    val dateTime = date.time
    val waitForScheduleCarList = projectCarScheduleInfoList.filter { it.autoScheduleType == AutoScheduleType.WaitForSlagSiteSchedule }.sortedBy {
        val projectDiggingMachineId = it.projectDiggingMachineId
        projectDiggingMachineScheduleInfoList.filter { it.projectDiggingMachineId == projectDiggingMachineId }.firstOrNull()?.distance?: 0
    }.toMutableList()
    if(projectSlagSiteScheduleInfoList.isEmpty()) throw Exception("渣场数量为0")
    if(waitForScheduleCarList.isEmpty()) throw Exception("待分配车数为0")

    val rt = mutableListOf<ProjectCarScheduleInfo>()
    val waitForScheduleCarListAllSize = waitForScheduleCarList.size
    while(waitForScheduleCarList.firstOrNull() != null) {
        val it = waitForScheduleCarList.firstOrNull()!!
        val projectDiggingMachineId = it.projectDiggingMachineId
        val tDate = Date(dateTime + waitForScheduleCarListAllSize - waitForScheduleCarList.size)
        rt.add(it.apply {
            this.autoScheduleType = AutoScheduleType.SlagSite
            this.slagSiteTime = tDate
            this.slagSiteId = (projectSlagSiteScheduleInfoList.filter { (dateTime - it.lastScheduleTime.time) >= it.intervalTimeLong }.sortedBy { it.distance + projectDiggingMachineScheduleInfoList.filter { it.projectDiggingMachineId == projectDiggingMachineId }.firstOrNull()!!.distance }.firstOrNull()
                    ?: projectSlagSiteScheduleInfoList.sortedBy { it.lastScheduleTime }.firstOrNull()!!).apply {
                this.lastScheduleTime = tDate
            }.slagSiteId
        })
        waitForScheduleCarList.remove(it)
    }

    rt.addAll(waitForScheduleCarList)
    return rt.toList()
}


data class AutoScheduleInfo (
        var projectCarScheduleInfoList: ArrayList<ProjectCarScheduleInfo> = arrayListOf(), //渣车信息
        var projectDiggingMachineScheduleInfoList: ArrayList<ProjectDiggingMachineScheduleInfo> = arrayListOf(), //挖机信息
        var projectSlagSiteScheduleInfoList: ArrayList<ProjectSlagSiteScheduleInfo> = arrayListOf(), //渣场信息
        var workScheduleInfo: WorkScheduleInfo = WorkScheduleInfo()  //上班信息
)

data class WorkScheduleInfo (
        var projectId: Long = 0L,    //参与的项目id

        var groupCode: String = "", //分组编号

        var waitTimeLong: Long = defaultWaitTimeLong,    //等待排班时间

        var startTime: Date = Date(0)   //开始上班时间
)


data class ProjectCarScheduleInfo (
        var projectId: Long = 0L,    //参与的项目id

        var groupCode: String = "", //分组编号

        var autoScheduleType: AutoScheduleType = AutoScheduleType.DiggingMachine,    //当前自动排班的类型

        var projectCarId: Long = 0L,    //渣车id

        var projectDiggingMachineId: Long = 0L,    //当前分配的挖机id

        var diggingMachineTime: Date = Date(0),   //分配挖机的时间

        var slagSiteId: Long = 0L,    //当前分配的渣场id

        var slagSiteTime: Date = Date(0)   //分配渣场的时间
)

data class ProjectDiggingMachineScheduleInfo (
        var projectId: Long = 0L,    //参与的项目id

        var groupCode: String = "", //分组编号

        var projectDiggingMachineId: Long = 0L,    //挖机id

        var intervalTimeLong: Long = defaultDiggingMachineIntervalLong, //挖机分配渣车的间隔时长/ms

        var distance: Long = defaultDiggingMachineDistance, //挖机到工作地点出入口的距离/米

        var lastScheduleTime: Date = Date(0),   //最后分配时间

        var firstScheduleTime: Date? = null,   //最前分配时间

        var compareValue: BigDecimal = BigDecimal.ZERO,   //

        var waitNum: Int = 0,

        var realWaitNum: Int = 0
)

data class ProjectDiggingMachineScheduleWaitInfo (

        var projectDiggingMachineId: Long = 0L,    //挖机id

        var projectDiggingMachineCode: String = "",    //挖机id

        var carWaitList: List<ProjectScheduleDetail> = listOf(),

        var carWaitNearList: List<ProjectScheduleDetail> = listOf(),

        var carWaitNotNearList: List<ProjectScheduleDetail> = listOf(),

        var nearDistance: BigDecimal = BigDecimal.ZERO
)

data class ProjectSlagSiteScheduleInfo (
        var projectId: Long = 0L,    //参与的项目id

        //var groupCode: String = "", //分组编号

        var slagSiteId: Long = 0L,    //渣场id

        var intervalTimeLong: Long = defaultSlagSiteIntervalLong, //渣场分配渣车的间隔时长/ms

        var distance: Long = defaultSlagSiteDistance, //渣场到工作地点出入口的距离/米

        var lastScheduleTime: Date = Date(0)   //最后分配时间
)

enum class AutoScheduleType(val remark: String) {
    WaitForDiggingMachineSchedule("待分配挖机"),
    WaitForSlagSiteSchedule("待分配渣场"),
    DiggingMachine("已分配挖机"),
    SlagSite("已分配渣场"),
    Unknow("未知");
}

fun <T> List<T>.toArrayList(): ArrayList<T> {
    val rt = arrayListOf<T>()
    this.forEach {
        rt.add(it)
    }
    return rt
}

fun getDigWaitAutoInfo(carDetailList: List<ProjectScheduleDetail>): List<ProjectDiggingMachineScheduleWaitInfo>{
    val realCarList = carDetailList.filter { it.autoScheduleType == AutoScheduleType.DiggingMachine }
    return realCarList.map { it.projectDiggingMachineId }.toSet().map {
        val projectDiggingMachineId = it
        val dig = carDetailList.filter { it.projectDiggingMachineId == projectDiggingMachineId }.firstOrNull()
        ProjectDiggingMachineScheduleWaitInfo().apply {
            val digId = it
            this.projectDiggingMachineId = it
            this.projectDiggingMachineCode = dig?.diggingMachineCode?: ""
            this.nearDistance = nearDistance
            this.carWaitList = realCarList.filter { it.projectDiggingMachineId == digId }
            this.carWaitNearList = carWaitList //carWaitList.filter { (it.nearDig?: false) }
            val carWaitNearCarIdList = carWaitNearList.map { it.projectCarId }
            this.carWaitNotNearList = carWaitList.filter { !carWaitNearCarIdList.contains(it.projectCarId) }
        }
    }
}

fun getDigNearWaitAutoInfo(carDetailList: List<ProjectScheduleDetail>): List<ProjectDiggingMachineScheduleWaitInfo>{
    val realCarList = carDetailList.filter { it.autoScheduleType == AutoScheduleType.DiggingMachine && (it.realDigDistance != null && it.realDigDistance!! > BigDecimal.ZERO && it.realDigDistance!! < BigDecimal(0.2)) }
    return realCarList.map { it.projectDiggingMachineId }.toSet().map {
        val projectDiggingMachineId = it
        val dig = carDetailList.filter { it.projectDiggingMachineId == projectDiggingMachineId }.firstOrNull()
        ProjectDiggingMachineScheduleWaitInfo().apply {
            val digId = it
            this.projectDiggingMachineId = it
            this.projectDiggingMachineCode = dig?.diggingMachineCode?: ""
            this.nearDistance = nearDistance
            this.carWaitList = realCarList.filter { it.projectDiggingMachineId == digId }
            this.carWaitNearList = carWaitList //carWaitList.filter { (it.nearDig?: false) }
            val carWaitNearCarIdList = carWaitNearList.map { it.projectCarId }
            this.carWaitNotNearList = carWaitList.filter { !carWaitNearCarIdList.contains(it.projectCarId) }
        }
    }
}

fun getCanReAutoCarList(carDetailList: List<ProjectScheduleDetail>): List<ProjectScheduleDetail>{
    val realCarList = carDetailList.filter { it.autoScheduleType == AutoScheduleType.DiggingMachine }
    return realCarList.filter {
        !(it.reAuto?: false) && (it.realDigDistance?: BigDecimal.ZERO) > nearDistance
    }
}


val nearDistance = BigDecimal(0.03)
val defaultDistance = BigDecimal(1)