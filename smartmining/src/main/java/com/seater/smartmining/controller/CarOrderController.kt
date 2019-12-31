package com.seater.smartmining.controller


import com.seater.smartmining.constant.SmartminingConstant
import com.seater.smartmining.dao.ProjectScheduleDetailDaoI
import com.seater.smartmining.entity.*
import com.seater.smartmining.entity.repository.CarOrderRepository
import com.seater.smartmining.entity.repository.ProjectCarRepository
import com.seater.smartmining.entity.repository.ProjectDiggingMachineRepository
import com.seater.smartmining.entity.repository.ScheduleMachineRepository
import com.seater.smartmining.enums.DiggingMachineStatus
import com.seater.smartmining.enums.DiggingMachineStopStatus
import com.seater.smartmining.enums.ProjectCarStatus
import com.seater.smartmining.exception.SmartminingProjectException
import com.seater.smartmining.service.CarOrderServiceI
import com.seater.smartmining.service.ProjectCarServiceI
import com.seater.smartmining.service.ScheduleCarServiceI
import com.seater.smartmining.service.impl.SendType
import com.seater.smartmining.utils.schedule.AutoScheduleType
import com.seater.user.entity.SysUser
import com.seater.user.util.PermissionUtils
import com.seater.user.util.constants.Constants
import com.sytech.user.helpers.defaultPageSize
import org.apache.shiro.SecurityUtils
import org.springframework.data.domain.PageRequest
import org.springframework.messaging.handler.annotation.Header
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

/**
 * CarOrder
 */
@RestController
@javax.transaction.Transactional(rollbackOn = [Exception::class])
@RequestMapping("/api/carOrder")
class CarOrderController(
        val carOrderServiceI: CarOrderServiceI,
        val carOrderRepository: CarOrderRepository,
        val scheduleCarServiceI: ScheduleCarServiceI,
        val projectCarServiceI: ProjectCarServiceI,
        val projectCarRepository: ProjectCarRepository,
        val projectScheduleDetailDaoI: ProjectScheduleDetailDaoI,
        val projectDiggingMachineRepository: ProjectDiggingMachineRepository,
        val scheduleMachineRepository: ScheduleMachineRepository
) {
//    /**
//     * @Description 查询 CarOrder
//     * @see CarOrder
//     * @url /api/carOrder/query
//     * @paramInfo |参数名|必选|类型|说明|
//     * @return <code></code>
//     */
//    @RequestMapping("/query")
//    fun query(
//            current: Int?,
//            pageSize: Int?
//    ): Any? {
//        val rt = carOrderServiceI.query(
//                pageable = PageRequest.of(current?: 0, pageSize?: defaultPageSize)
//        )
//        return mapOf(
//                "content" to rt.content.map {
//                    mapOf(
//                            "it" to it
//                    )
//                },
//                "totalElements" to rt.totalElements
//        )
//    }

    fun init(carOrder: CarOrder): CarOrder {
        return carOrderServiceI.init(carOrder)
    }

    /**
     * @Description 新增 CarOrder(临时)
     * @see CarOrder
     * @url /api/carOrder/addTemp
     * @return {"status":"success","message":""}
     */
    @RequestMapping("/addTemp")
    fun addTemp(
            carId: Long,
            orderTime: Date?,
            diggingMachineId: Long?,
            slagSiteId: Long?,
            @RequestHeader(value = "projectId") projectId: Long?
    ): Any? {
        carOrderRepository.findByCarIdAndCarOrderStateNot(carId, CarOrderState.End).filter {
            it.carOrderType == CarOrderType.Temp
        }.forEach {
            carOrderServiceI.cancel(it)
        }
        val car = projectCarServiceI.get(carId)
        val rt = carOrderServiceI.add(init(CarOrder().apply {
            this.carId = carId
            this.carUid = car.uid
            this.projectId = projectId
            this.orderTime = orderTime ?: Date()
            this.carOrderType = CarOrderType.Temp
            if (diggingMachineId != null && diggingMachineId > 0L) this.diggingMachineId = diggingMachineId
            else if (slagSiteId != null && slagSiteId > 0L) this.slagSiteId = slagSiteId
            else throw Exception("缺少参数")
            if (this.diggingMachineId != null) this.fixDig = true
            if (this.slagSiteId != null) this.fixSite = true
        }))
        val sc = scheduleCarServiceI.getAllByProjectIdAndCarIdAndIsVaild(projectId ?: 0L, rt.carId, true).firstOrNull()
        carOrderServiceI.sendCar(rt.carId, sc?.groupCode ?: "", Date().time % 1000, projectId
                ?: 0L, AutoScheduleType.WaitForDiggingMachineSchedule, SendType.request, CarOrderType.Temp, null)
        return """{"status":"success","message":""}"""
    }

    /**
     * @Description 新增 CarOrder(辅助)
     * @see CarOrder
     * @url /api/carOrder/addText
     * @return {"status":"success","message":""}
     */
    @RequestMapping("/addText")
    fun addText(
            carId: Long,
            orderTime: Date?,
            message: String,
            @RequestHeader(value = "projectId") projectId: Long?
    ): Any? {
        val car = projectCarServiceI.get(carId)
        val rt = carOrderServiceI.add(init(CarOrder().apply {
            this.carId = carId
            this.carUid = car.uid
            this.projectId = projectId
            this.orderTime = orderTime ?: Date()
            this.carOrderType = CarOrderType.Text
            if (!message.isNullOrBlank()) this.message = message
            else throw Exception("消息为空")
            this.sendTime = Date()
        }))
        val sc = scheduleCarServiceI.getAllByProjectIdAndCarIdAndIsVaild(projectId ?: 0L, rt.carId, true).firstOrNull()
        carOrderServiceI.sendCar(rt.carId, sc?.groupCode ?: "", Date().time % 1000, projectId
                ?: 0L, AutoScheduleType.WaitForDiggingMachineSchedule, SendType.request, CarOrderType.Text, null)
        return """{"status":"success","message":""}"""
    }

    /**
     * @Description 新增 CarOrder(固定)
     * @see CarOrder
     * @url /api/carOrder/addFix
     * @return {"status":"success","message":""}
     */
    @RequestMapping("/addFix")
    fun addFix(
            carId: Long,
            orderTime: Date?,
            diggingMachineId: Long?,
            slagSiteId: Long?,
            @RequestHeader(value = "projectId") projectId: Long?
    ): Any? {
        val carOrderList = carOrderRepository.findByCarIdAndCarOrderStateNot(carId, CarOrderState.End).filter {
            it.carOrderType == CarOrderType.Fix
        }
        val digOrder = carOrderList.filter { diggingMachineId != null && it.diggingMachineId == diggingMachineId }.firstOrNull()
        val siteOrder = carOrderList.filter { slagSiteId != null && it.slagSiteId == slagSiteId }.firstOrNull()
        carOrderList.filter {
            it.diggingMachineId != (digOrder?.id ?: 0L) && it.slagSiteId != (siteOrder?.id ?: 0L)
        }.forEach {
            carOrderServiceI.cancel(it)
        }
        if ((diggingMachineId != null && diggingMachineId > 0L && digOrder == null) || (diggingMachineId != null && diggingMachineId > 0L && siteOrder == null)) {
            val car = projectCarServiceI.get(carId)
            val rt = carOrderServiceI.add(init(CarOrder().apply {
                this.carId = carId
                this.carUid = car.uid
                this.projectId = projectId
                this.orderTime = orderTime ?: Date()
                this.carOrderType = CarOrderType.Fix
                this.diggingMachineId = diggingMachineId
                this.slagSiteId = slagSiteId
                if (this.diggingMachineId != null) this.fixDig = true
                if (this.slagSiteId != null) this.fixSite = true
            }))
            val sc = scheduleCarServiceI.getAllByProjectIdAndCarIdAndIsVaild(projectId
                    ?: 0L, rt.carId, true).firstOrNull()
            carOrderServiceI.sendCar(rt.carId, sc?.groupCode ?: "", Date().time % 1000, projectId
                    ?: 0L, AutoScheduleType.WaitForDiggingMachineSchedule, SendType.request, CarOrderType.Fix, null)
        } else throw  Exception("参数错误")
        return """{"status":"success","message":""}"""
    }


    /**
     * @Description 查询
     * @see CarText
     * @url /api/carOrder/query
     * @paramInfo |参数名|必选|类型|说明|
     * @return <code></code>
     */
    @RequestMapping("/query")
    fun query(
            current: Int?,
            pageSize: Int?,
            projectId: Long?
    ): Any? {
        //判断是查询全部还是筛选
//        var flag = false
//        val jsonArray = PermissionUtils.getProjectPermission(projectId)
//                ?: throw SmartminingProjectException("该用户没有任何权限")
//        if (jsonArray.contains(SmartminingConstant.ALLDATA))
//            flag = true
//
//        //获取当前用户对象
        val managerId = null // if(flag) null else (SecurityUtils.getSubject().session.getAttribute(Constants.SESSION_USER_INFO) as SysUser).id
        val rt = carOrderServiceI.queryPage(
                current, pageSize, projectId, managerId
        )
        return mapOf(
                "content" to rt.content.map {
                    mapOf(
                            "it" to if ((it.diggingMachineCode?.isNullOrBlank() ?: true)) {
                                val dig = projectDiggingMachineRepository.getById(it.diggingMachineId).firstOrNull()
                                it.copy().apply {
                                    this.diggingMachineCode = dig?.code ?: ""
                                }
                            } else it
                    )
                },
                "totalElements" to rt.totalElements
        )
    }
}