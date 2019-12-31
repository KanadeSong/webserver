/*
package com.seater.smartmining.schedule

import com.seater.smartmining.entity.*
import com.seater.smartmining.entity.repository.*
import com.seater.smartmining.enums.DiggingMachineStatus
import com.seater.smartmining.enums.DiggingMachineStopStatus
import com.seater.smartmining.enums.ProjectCarStatus
import com.seater.smartmining.enums.ProjectDeviceType
import com.seater.smartmining.service.*
import com.seater.smartmining.service.impl.SendType
import com.seater.smartmining.utils.schedule.*
import com.seater.user.dao.GlobalSet
import com.systech.helpers.redis.jsonSet
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.TimeUnit
import javax.persistence.EntityManager

@Component
@Transactional
class ProjectScheduleAutoUpdate(
        val em: EntityManager,
        val projectScheduleDetailServiceI: ProjectScheduleDetailServiceI,
        val projectServiceI: ProjectServiceI,
        val projectScheduleServiceI: ProjectScheduleServiceI,
        val carOrderServiceI: CarOrderServiceI,
        val carOrderRepository: CarOrderRepository,
        val projectCarRepository: ProjectCarRepository,
        val projectDiggingMachineServiceI: ProjectDiggingMachineServiceI,
        val scheduleMachineServiceI: ScheduleMachineServiceI,
        val projectDiggingMachineEfficiencyServiceI: ProjectDiggingMachineEfficiencyServiceI,
        val projectCarWorkInfoRepository: ProjectCarWorkInfoRepository,
        val projectDeviceRepository: ProjectDeviceRepository,
        val scheduleCarServiceI: ScheduleCarServiceI,
        val stringRedisTemplate: StringRedisTemplate,
        val projectScheduleRepository: ProjectScheduleRepository,
        val projectDiggingMachineRepository: ProjectDiggingMachineRepository
) {
    val log = LoggerFactory.getLogger(this.javaClass)
    var redisCacheTimeout = 1000*60*2L
    val keyGroup = "entity:AutoDigLock:"
    fun getKeyLockDig(id: Long) = "${keyGroup}${id}"
    val valueOps by lazy {
        stringRedisTemplate.opsForValue()
    }

    @Scheduled(cron = "0 0/3 * * * ?")
    fun scheduleAutoUpdate() {
        val digAll = mutableListOf<ProjectDiggingMachine>()
        val infoAll = mutableListOf<ProjectCarWorkInfo>()
        val date = Date()
        val ldate = Date(date.time - 1000 * 60 * 60)
        val mdate = Date(date.time - 1000 * 60 * 30)
        projectServiceI.all.forEach {
            digAll.addAll(
                    projectDiggingMachineServiceI.getAllByProjectIdAndIsVaildAndSelected(it.id, true)
            )
            infoAll.addAll(
                    projectCarWorkInfoRepository.findByProjectIdAndTimeLoadBetween(it.id, ldate, date)
            )
        }
        val effAll = projectDiggingMachineEfficiencyServiceI.queryPage(
                current = 0,
                pageSize = 10000,
                projectId = null,
                groupCode = null,
                projectDiggingMachineId = null
        ).content
        digAll.forEach {
            //scheduleMachineServiceI.getAllByProjectIdAndGroupCode(it.projectId, it.groupCode).forEach {
            val pid = it.projectId
            val mid = it.id //machineId
            val dig = it //digAll.filter { it.projectId == pid && it.id == mid }.firstOrNull()
            if (dig != null) {
                val new = effAll.filter { it.projectId == pid && it.projectDiggingMachineId == mid }.sortedByDescending { it.id }.firstOrNull()
                        ?: ProjectDiggingMachineEfficiency(projectId = pid, projectDiggingMachineId = mid)
                projectDiggingMachineEfficiencyServiceI.save(
                        new.apply {
                            if (this.lastHourCarNum <= 0) this.lastHourCarNum = dig.defaultCapacity
                            val infoList = infoAll.filter { it.projectId == pid && it.diggingMachineId == mid }
                            if ((infoList.minBy { it.createDate }?.createDate
                                            ?: date) < mdate) this.lastHourCarNum = infoList.size
                            if (this.lastHourCarNum < dig.minCapacity) this.lastHourCarNum = dig.minCapacity
                            if (this.lastHourCarNum > dig.maxCapacity) this.lastHourCarNum = dig.maxCapacity
                        }
                )
            }
            //}
        }
    }

    */
/**
     * 在线离线判断
     *//*

    @Scheduled(cron = "0 0/3 * * * ?")
    fun scheduleDetailAutoUpdate() {
        projectServiceI.all.forEach {
            projectScheduleDetailServiceI.queryPage(
                    current = null,
                    pageSize = 10000,
                    groupCode = null,
                    projectId = it.id,
                    disable = null,
                    showReady = null,
                    managerId = null
            ).filter { it.autoScheduleType == AutoScheduleType.DiggingMachine }.forEach {
                val car = projectCarRepository.getById(it.projectCarId).firstOrNull()
                var isDo = false
                it.disable = false
                if (car != null) {
                    val deviceCar = projectDeviceRepository.getAllByCodeAndDeviceType(car.code, ProjectDeviceType.SlagTruckDevice.ordinal).firstOrNull()
                    val scheduleCar = scheduleCarServiceI.getAllByProjectIdAndCarIdAndIsVaild(it.projectId, car.id, true).filter { !it.fault }.firstOrNull()
                    if (deviceCar != null && scheduleCar != null) {
                        isDo = true
                        if (deviceCar.status != ProjectDeviceStatus.OnLine) {
                            projectScheduleDetailServiceI.save(
                                    it.apply {
                                        this.disable = true
                                        this.offlineIs = true
                                    }
                            )
                        }
//                        else if(!deviceCar.vaild || !(car.status == ProjectCarStatus.Working && car.vaild && car.seleted)){
//                            projectScheduleDetailServiceI.save(
//                                    it.apply {
//                                        this.disable = true
//                                    }
//                            )
//                        }
                    }
                }
//                if(!isDo){
//                    projectScheduleDetailServiceI.save(
//                            it.apply {
//                                this.disable = true
//                            }
//                    )
//                }
//                else{
//                    projectScheduleDetailServiceI.save(
//                            it
//                    )
//                }
            }
        }
    }



    */
/**
     * 重新排班
     *//*

    @Scheduled(cron = "0/5 * * * * ?")
    fun scheduleDetailReAutoUpdate() {
        //println("---------------------------------------test--scheduleDetailReAutoUpdate---------------------------------------------")
        projectServiceI.all.forEach {
            val pid = it.id
            val scheduleAutoList = projectScheduleRepository.findByProjectIdAndDispatchMode(it.id, ProjectDispatchMode.Auto)
            val ggroupCodeList = scheduleAutoList.map { it.groupCode }.toSet()
            ggroupCodeList.forEach {
                val scheduleMachineList = scheduleMachineServiceI.getAllByProjectIdAndGroupCode(pid, it).filter {
                    val dig = projectDiggingMachineRepository.getById(it.machineId).filter { it.status == DiggingMachineStatus.Working && it.stopStatus == DiggingMachineStopStatus.Normal }.firstOrNull()
                    dig != null
                }
                var sdList = projectScheduleDetailServiceI.queryPage(
                        current = null,
                        pageSize = 10000,
                        groupCode = it,
                        projectId = pid,
                        disable = false,
                        showReady = null,
                        managerId = null
                ).content.filter { it.autoScheduleType == AutoScheduleType.DiggingMachine }

                var projectDiggingMachineScheduleWaitInfoList = getDigWaitAutoInfo(sdList)
                val t = scheduleMachineList.map {
                    val projectDiggingMachineId = it.machineId
                    projectDiggingMachineScheduleWaitInfoList.filter { it.projectDiggingMachineId == projectDiggingMachineId }.firstOrNull()?: ProjectDiggingMachineScheduleWaitInfo().apply{
                        this.projectDiggingMachineId = projectDiggingMachineId
                    }
                }
                projectDiggingMachineScheduleWaitInfoList = t
                if(projectDiggingMachineScheduleWaitInfoList.size > 0){
                    val min = projectDiggingMachineScheduleWaitInfoList.minBy { it.carWaitList.size }!!
                    val max = projectDiggingMachineScheduleWaitInfoList.maxBy { it.carWaitList.size }!!
                    if(min.carWaitList.size <= 2 && max.carWaitList.size >= 3 && (max.carWaitList.size - min.carWaitList.size) >= 2){
                        val carWaitList = max.carWaitList
                        val minDis = carWaitList.minBy { (it.realDigDistance?.toDouble()?: defaultDistance.toDouble()) }!!
                        var newList = carWaitList.filter { (it.realDigDistance?: defaultDistance) < BigDecimal(100) }.sortedBy { it.realDigDistance }.filter{
//                            (val c = carOrderServiceI.get(it.carOrderId)
//                            if(c == null) true
//                            else !(c.reAuto?: false))
                            (!(it.reAuto?: false))
                        }
                        newList = carWaitList.sortedByDescending { it.carOrderTime }.filter{
                            (!(it.reAuto?: false) && (!(it.secondAuto?: false))) || (min.carWaitList.size <= 0)
                        }
                        if(newList.size >= 1) {
                            var reAutoCar = newList.first()
                            val sortedList = newList //listOf(newList.get(0), newList.get(1), newList.get(2))
//                            if ((reAutoCar.realDigDistance ?: defaultDistance) <= (minDis.realDigDistance
//                                            ?: defaultDistance)) {
//                                reAutoCar = sortedList.sortedByDescending { it.carOrderTime }.firstOrNull()!!
//                            }
                            val oldOrderCar = sortedList.minBy { it.carOrderTime }!!
                            if(reAutoCar.carOrderTime > oldOrderCar.carOrderTime ) {
                                val realReAutoCar = reAutoCar
                                val rt = carOrderServiceI.save(CarOrder().apply {
                                    this.carId = realReAutoCar.projectCarId
                                    this.carCode = realReAutoCar.carCode
                                    this.carUid = realReAutoCar.carUid
                                    this.projectId = realReAutoCar.projectId
                                    this.orderTime = orderTime ?: Date()
                                    this.carOrderType = CarOrderType.Temp
                                    this.reAuto = true
                                    this.reAutoTime = Date()
                                    this.diggingMachineId = min.projectDiggingMachineId
                                    this.diggingMachineCode = min.projectDiggingMachineCode
                                    this.groupCode = realReAutoCar.groupCode
                                    this.autoScheduleType = AutoScheduleType.DiggingMachine
                                    //this.slagSiteId = min
                                    this.remark = "系统"
                                    this.fromUserName = "系统"
                                    this.fixDig = true
                                    this.realDigId = realReAutoCar.realDigId
                                    this.realDigDistance = realReAutoCar.realDigDistance
                                    this.realSiteIdList = realReAutoCar.realSiteIdList
                                    this.realSiteDistanceList = realReAutoCar.realSiteDistanceList
                                })
                                //println("----------------------------testauto-----------------------------------------rt:${rt}")
                                carOrderServiceI.sendCar(rt.carId, rt.groupCode ?: "", Date().time % 1000, rt.projectId
                                        ?: 0L, AutoScheduleType.WaitForDiggingMachineSchedule, SendType.request, CarOrderType.Temp, realReAutoCar)
                            }
                        }
                    }
                }
            }
        }

    }

    */
/**
     * 重新排班(暂停)
     *//*

    @Scheduled(cron = "0/5 * * * * ?")
    fun scheduleDetailReAutoPauseUpdate() {
        //println("---------------------------------------test--scheduleDetailReAutoUpdate---------------------------------------------")
        projectServiceI.all.forEach {
            val pid = it.id
            val scheduleAutoList = projectScheduleRepository.findByProjectIdAndDispatchMode(it.id, ProjectDispatchMode.Auto)
            val ggroupCodeList = scheduleAutoList.map { it.groupCode }.toSet()
            ggroupCodeList.forEach {
                val scheduleMachineList = scheduleMachineServiceI.getAllByProjectIdAndGroupCode(pid, it).filter {
                    val dig = projectDiggingMachineRepository.getById(it.machineId).filter { it.status == DiggingMachineStatus.Working && it.stopStatus == DiggingMachineStopStatus.PAUSE }.firstOrNull()
                    dig != null
                }
                if(scheduleMachineList.size > 0) {
                    val scheduleMachineIdList = scheduleMachineList.map { it.machineId }
                    var sdList = projectScheduleDetailServiceI.queryPage(
                            current = null,
                            pageSize = 10000,
                            groupCode = it,
                            projectId = pid,
                            disable = false,
                            showReady = null,
                            managerId = null
                    ).content.filter { it.autoScheduleType == AutoScheduleType.DiggingMachine && scheduleMachineIdList.contains(it.projectDiggingMachineId) }
                    sdList.filter { !(it.nearDig ?: false) }.forEach {
                        projectScheduleDetailServiceI.save(
                                it.apply {
                                    this.reAuto = true
                                    this.autoScheduleType = AutoScheduleType.WaitForDiggingMachineSchedule
                                    this.projectDiggingMachineId = 0L
                                    this.projectSlagSiteId = 0L
                                    this.carOrderId = 0L
                                    this.changeTime = Date()
                                }
                        )
                    }
                }
            }
        }

    }

    */
/**
     * 二次排班
     *//*

    @Scheduled(cron = "0/3 * * * * ?")
    fun scheduleDetailSecondAutoUpdate() {
        //println("---------------------------------------test--scheduleDetailReAutoUpdate---------------------------------------------")
        projectServiceI.all.forEach {
            val pid = it.id
            val scheduleAutoList = projectScheduleRepository.findByProjectIdAndDispatchMode(it.id, ProjectDispatchMode.Auto)
            val ggroupCodeList = scheduleAutoList.map { it.groupCode }.toSet()
            ggroupCodeList.forEach {
                val scheduleMachineList = scheduleMachineServiceI.getAllByProjectIdAndGroupCode(pid, it).filter {
                    val dig = projectDiggingMachineRepository.getById(it.machineId).filter { it.status == DiggingMachineStatus.Working && it.stopStatus == DiggingMachineStopStatus.Normal }.firstOrNull()
                    dig != null
                }
                var sdList = projectScheduleDetailServiceI.queryPage(
                        current = null,
                        pageSize = 10000,
                        groupCode = it,
                        projectId = pid,
                        disable = false,
                        showReady = null,
                        managerId = null
                ).content.filter { it.autoScheduleType == AutoScheduleType.DiggingMachine }

                var projectDiggingMachineScheduleWaitInfoList = getDigNearWaitAutoInfo(sdList)
                val t = scheduleMachineList.map {
                    val projectDiggingMachineId = it.machineId
                    projectDiggingMachineScheduleWaitInfoList.filter { it.projectDiggingMachineId == projectDiggingMachineId }.firstOrNull()?: ProjectDiggingMachineScheduleWaitInfo().apply{
                        this.projectDiggingMachineId = projectDiggingMachineId
                    }
                }
                projectDiggingMachineScheduleWaitInfoList = t
                if(projectDiggingMachineScheduleWaitInfoList.size > 0){
                    val min = projectDiggingMachineScheduleWaitInfoList.minBy { it.carWaitList.size }!!
                    val max = projectDiggingMachineScheduleWaitInfoList.maxBy { it.carWaitList.size }!!
                    if(max.carWaitList.size >= 3 && (max.carWaitList.size - min.carWaitList.size) >= 2){
                        val carWaitList = max.carWaitList
                        val minDis = carWaitList.minBy { (it.realDigDistance?.toDouble()?: defaultDistance.toDouble()) }!!
                        var newList = carWaitList.filter { (it.realDigDistance?: defaultDistance) < BigDecimal(100) }.sortedBy { it.realDigDistance }.filter{
                            //                            (val c = carOrderServiceI.get(it.carOrderId)
//                            if(c == null) true
//                            else !(c.reAuto?: false))
                            (!(it.reAuto?: false)) && (!(it.secondAuto?: false)) && (!(it.nearDig?: false))
                        }
                        if(carWaitList.size >= 3 && newList.size > 0) {
                            var reAutoCar = newList.lastOrNull()!!
                            val oldOrderCar = carWaitList.minBy { it.carOrderTime }!!
                            if(reAutoCar.carOrderTime > oldOrderCar.carOrderTime ) {
                                val realReAutoCar = reAutoCar
                                val rt = carOrderServiceI.save(CarOrder().apply {
                                    this.carId = realReAutoCar.projectCarId
                                    this.carCode = realReAutoCar.carCode
                                    this.carUid = realReAutoCar.carUid
                                    this.projectId = realReAutoCar.projectId
                                    this.orderTime = orderTime ?: Date()
                                    this.carOrderType = CarOrderType.Temp
                                    this.secondAuto = true
                                    this.secondAutoTime = Date()
                                    this.diggingMachineId = min.projectDiggingMachineId
                                    this.diggingMachineCode = min.projectDiggingMachineCode
                                    this.groupCode = realReAutoCar.groupCode
                                    this.autoScheduleType = AutoScheduleType.DiggingMachine
                                    //this.slagSiteId = min
                                    this.remark = "系统"
                                    this.fromUserName = "系统"
                                    this.fixDig = true
                                    this.realDigId = realReAutoCar.realDigId
                                    this.realDigDistance = realReAutoCar.realDigDistance
                                    this.realSiteIdList = realReAutoCar.realSiteIdList
                                    this.realSiteDistanceList = realReAutoCar.realSiteDistanceList
                                })
                                //println("----------------------------testauto-----------------------------------------rt:${rt}")
                                carOrderServiceI.sendCar(rt.carId, rt.groupCode ?: "", Date().time % 1000, rt.projectId
                                        ?: 0L, AutoScheduleType.WaitForDiggingMachineSchedule, SendType.request, CarOrderType.Temp, realReAutoCar)
                            }
                        }
                    }
                }
            }
        }

    }

    */
/**
     * 锁定挖机
     *//*

    //@Scheduled(cron = "0/10 * * * * ?")
    fun scheduleDetailLockDigUpdate() {
        projectServiceI.all.forEach {
            val pid = it.id
            val digList = projectScheduleRepository.findByProjectIdAndDispatchMode(pid, ProjectDispatchMode.Auto)
            val scheduleMachineList = scheduleMachineServiceI.getAllByProjectId(pid)
            val pdList = projectDiggingMachineServiceI.getByProjectIdOrderById(pid)
            digList.forEach {
                val groupCode = it.groupCode
                val smList = scheduleMachineList.filter { it.groupCode == groupCode }
                smList.forEach {
                    val pmid = it.machineId
                    val dig = pdList.filter { it.id == pmid }.firstOrNull()
                    if(dig != null) {
                        val key = getKeyLockDig(dig.id)
                        if (it.fault || !it.isVaild || dig.status != DiggingMachineStatus.Working || dig.stopStatus == DiggingMachineStopStatus.Normal) {
                            valueOps.jsonSet(key, 0, redisCacheTimeout, TimeUnit.MILLISECONDS)
                        }
                    }
                }
            }
        }

    }
}*/
