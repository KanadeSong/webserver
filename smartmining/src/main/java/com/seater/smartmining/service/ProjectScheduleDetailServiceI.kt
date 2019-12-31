package com.seater.smartmining.service

import com.seater.smartmining.entity.*
import com.seater.smartmining.service.impl.SendType
import com.seater.smartmining.utils.schedule.AutoScheduleType
import com.sytech.user.helpers.defaultPageSize
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.querydsl.QPageRequest
import org.springframework.messaging.Message

interface ProjectScheduleDetailServiceI {
    fun get(id: Long?): ProjectScheduleDetail?
    fun getUsed(id: Long?): ProjectScheduleDetail?
    fun save(projectScheduleDetail: ProjectScheduleDetail): ProjectScheduleDetail
    fun delete(id: Long)
    fun delete(ids: List<Long>)

    fun query(spec: Specification<ProjectScheduleDetail>? = null, pageable: Pageable = QPageRequest(0, defaultPageSize)): Page<ProjectScheduleDetail>

    /**
     *
     */


    fun queryPage(
            current: Int?,
            pageSize: Int?,
            groupCode: String?,
            projectId: Long?,
            disable: Boolean?,
            showReady: Boolean?,
            managerId: Long?
    ): Page<ProjectScheduleDetail>

    fun reset(
            groupCode: String,
            projectId: Long
    ): List<ProjectScheduleDetail>?

    fun clean(
    ): Boolean

    fun findByProjectCarIdAndProjectIdOrderByIdDesc(projectCarId: Long, projectId: Long): List<ProjectScheduleDetail>

    fun initByWorkInfo(projectCarWorkInfo: ProjectCarWorkInfo)
    fun initByCar(projectCar: ProjectCar)

    fun initByFix(carUid: String, machineId: String, payload: String, isNear: Boolean)

    fun initByFixLoad(carUid: String, machineId: String, payload: String)

    fun replyCar(car: ProjectCar, scheduleCar: ScheduleCar, rid: Long, projectId: Long, autoScheduleType: AutoScheduleType, sendType: SendType, carOtherType: CarOrderType)

    fun isAuto(carUid: String): Boolean
}
