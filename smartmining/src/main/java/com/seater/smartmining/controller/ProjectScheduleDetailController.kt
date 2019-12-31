package com.seater.smartmining.controller

import com.seater.smartmining.constant.SmartminingConstant
import com.seater.smartmining.entity.CarOrder
import com.seater.smartmining.entity.ProjectDiggingMachine
import com.seater.smartmining.entity.ProjectDispatchMode
import com.seater.smartmining.entity.ProjectSchedule
import com.seater.smartmining.entity.repository.ProjectCarRepository
import com.seater.smartmining.entity.repository.ProjectDeviceRepository
import com.seater.smartmining.entity.repository.ProjectDiggingMachineRepository
import com.seater.smartmining.enums.ProjectDeviceType
import com.seater.smartmining.exception.SmartminingProjectException
import com.seater.smartmining.service.*
import com.seater.user.entity.SysUser
import com.seater.user.util.PermissionUtils
import com.seater.user.util.constants.Constants
import org.apache.shiro.SecurityUtils
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.persistence.criteria.Predicate

/**
 * ProjectScheduleDetail
 */
@RestController
@javax.transaction.Transactional(rollbackOn = [Exception::class])
@RequestMapping("/api/projectScheduleDetail")
class ProjectScheduleDetailController(
        val projectScheduleDetailServiceI: ProjectScheduleDetailServiceI,
        val carOrderServiceI: CarOrderServiceI,
        val projectScheduleServiceI: ProjectScheduleServiceI,
        val scheduleCarServiceI: ScheduleCarServiceI,
        val scheduleMachineServiceI: ScheduleMachineServiceI,
        val projectCarServiceI: ProjectCarServiceI,
        val projectDiggingMachineServiceI: ProjectDiggingMachineServiceI,
        val projectDiggingMachineRepository: ProjectDiggingMachineRepository,
        val projectDeviceRepository: ProjectDeviceRepository,
        val projectCarRepository: ProjectCarRepository
) {

    /**
     * @Description 查询 query
     * @url /api/projectScheduleDetail/query
     * @return <code></code>
     */
    @RequestMapping("/query")
    fun query(
            current: Int?,
            pageSize: Int?,
            groupCode: String?,
            @RequestHeader(value="projectId") projectId: Long?
    ): Any? {
//        //判断是查询全部还是筛选
//        var flag = false
//        val jsonArray = PermissionUtils.getProjectPermission(projectId)
//                ?: throw SmartminingProjectException("该用户没有任何权限")
//        if (jsonArray.contains(SmartminingConstant.ALLDATA))
//            flag = true

        //获取当前用户对象
        val managerId = null //if(flag) null else (SecurityUtils.getSubject().session.getAttribute(Constants.SESSION_USER_INFO) as SysUser).id

        val rt = projectScheduleDetailServiceI.queryPage(
                current = current,
                pageSize = pageSize,
                groupCode = groupCode,
                projectId = projectId,
                disable = false,
                showReady = false,
                managerId = managerId
        )
        val carList = projectCarServiceI.getAllByProjectIdAndSeleted(projectId!!, true)
        val digList = projectDiggingMachineServiceI.getAllByProjectIdAndIsVaildAndSelected(projectId!!, true)
        return mapOf(
                "content" to rt.content.map{
                    val carOrder = carOrderServiceI.get(it.carOrderId)
                    mapOf(
                            "it" to if(!it.carCode.isNullOrBlank() && !it.diggingMachineCode.isNullOrBlank()) it else {
                                val carId = it.projectCarId
                                val digId = it.projectDiggingMachineId
                                val car = carList.filter { it.id == carId }.firstOrNull()
                                val dig = digList.filter { it.id == digId }.firstOrNull()
                                it.copy().apply {
                                    this.carCode = car?.code?: ""
                                    this.carUid = car?.uid?: ""
                                    this.diggingMachineCode = dig?.code?: ""
                                }
                            },
                            "other" to mapOf(
                                    "carOrder" to carOrder?.copy()?.apply {
                                        if(this.carId > 0L){
                                            if(this.carCode.isNullOrBlank()){
                                                val car = projectCarRepository.getById(this.carId).firstOrNull()
                                                this.carCode = car?.code?: ""
                                            }
                                            if(!this.carCode.isNullOrBlank()) {
                                                val projectDevice = projectDeviceRepository.findByProjectIdAndCodeAndDeviceType(this.projectId, this.carCode, ProjectDeviceType.SlagTruckDevice).filter { it.vaild }.firstOrNull()
                                                this.carUid = projectDevice?.uid?: ""
                                                this.carCode = projectDevice?.code?: ""
                                            }
                                        }
                                        if(this.diggingMachineId != null && this.diggingMachineId!! > 0L && (this.diggingMachineCode == null || this.diggingMachineCode!!.isNullOrBlank())) {
                                            val dig = projectDiggingMachineRepository.getById(this.diggingMachineId).firstOrNull()
                                            if(dig != null){
                                                this.diggingMachineCode = dig.code
                                            }
                                        }
                                    }
                            )
                    )
                },
                "totalElements" to rt.totalElements
        )
    }

    /**
     * @Description 查询 query
     * @url /api/projectScheduleDetail/query
     * @return <code></code>
     */
    @RequestMapping("/querySchedule")
    fun querySchedule(
            current: Int?,
            pageSize: Int?,
            groupCode: String?,
            dispatchMode: ProjectDispatchMode?,
            @RequestHeader(value="projectId") projectId: Long?
    ): Any? {
        val minPageSize = 10
        val maxPageSize = Integer.MAX_VALUE

        var cur = (current ?: 0) - 1
        var page = pageSize ?: minPageSize
        if (cur < 0) cur = 0
        if (page < 0) page = maxPageSize
        else if (page > maxPageSize) page = maxPageSize

        var spec = Specification<ProjectSchedule> { root, query, cb ->
            var ls = mutableListOf<Predicate>()

            if (projectId != null) {
                ls.add(cb.equal(root.get<Long>("projectId"), projectId))
            }

            if (!groupCode.isNullOrBlank()) {
                ls.add(cb.equal(root.get<String>("groupCode"), groupCode))
            }

            if (dispatchMode != null) {
                ls.add(cb.equal(root.get<ProjectDispatchMode>("dispatchMode"), dispatchMode))
            }

            cb.and(*ls.toTypedArray())
        }
        val rt = projectScheduleServiceI.query(spec, PageRequest.of(cur, page, Sort(Sort.Direction.DESC, "id")))
        return mapOf(
                "content" to rt.content.map{
                    mapOf(
                            "it" to it,
                            "other" to mapOf(
                                    "scheduleCar" to scheduleCarServiceI.getAllByProjectIdAndGroupCode(it.projectId, it.groupCode),
                                    "scheduleMachine" to scheduleMachineServiceI.getAllByProjectIdAndGroupCode(it.projectId, it.groupCode)
                            )
                    )
                },
                "totalElements" to rt.totalElements
        )
    }

    /**
     * @Description 查询 query
     * @url /api/projectScheduleDetail/query
     * @return <code></code>
     */
    @RequestMapping("/queryDig")
    fun queryDig(
            current: Int?,
            pageSize: Int?,
            groupCode: String?,
            dispatchMode: ProjectDispatchMode?,
            @RequestHeader(value="projectId") projectId: Long?
    ): Any? {
        val minPageSize = 10
        val maxPageSize = Integer.MAX_VALUE

        var cur = (current ?: 0) - 1
        var page = pageSize ?: minPageSize
        if (cur < 0) cur = 0
        if (page < 0) page = maxPageSize
        else if (page > maxPageSize) page = maxPageSize

        var spec = Specification<ProjectDiggingMachine> { root, query, cb ->
            var ls = mutableListOf<Predicate>()

            if (projectId != null) {
                ls.add(cb.equal(root.get<Long>("projectId"), projectId))
            }

            if(true){

            }

            cb.and(*ls.toTypedArray())
        }
        val rt = projectDiggingMachineServiceI.query(spec, PageRequest.of(cur, page, Sort(Sort.Direction.DESC, "id")))
        return mapOf(
                "content" to rt.content.map{
                    mapOf(
                            "it" to it//,
//                            "other" to mapOf(
//                                    "scheduleCar" to scheduleCarServiceI.getAllByProjectIdAndGroupCode(it.projectId, it.groupCode),
//                                    "scheduleMachine" to scheduleMachineServiceI.getAllByProjectIdAndGroupCode(it.projectId, it.groupCode)
//                            )
                    )
                },
                "totalElements" to rt.totalElements
        )
    }
}