package com.seater.smartmining.service

import com.seater.smartmining.entity.CarOrder
import com.seater.smartmining.entity.CarOrderType
import com.seater.smartmining.entity.ProjectScheduleDetail
import com.seater.smartmining.service.impl.SendType
import com.seater.smartmining.utils.schedule.AutoScheduleType
import com.sytech.user.helpers.defaultPageSize
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import java.util.*

interface CarOrderServiceI {
    fun get(id: Long?): CarOrder?
    fun save(CarOrder: CarOrder): CarOrder
    fun add(CarOrder: CarOrder): CarOrder
    fun update(CarOrder: CarOrder, old: CarOrder): CarOrder
    fun delete(id: Long)
    fun delete(ids: List<Long>)
    fun query(spec: Specification<CarOrder>? = null, pageable: Pageable = PageRequest.of(0, defaultPageSize)): Page<CarOrder>
    fun cancel(carOrder: CarOrder): CarOrder
    fun down(carOrder: CarOrder): CarOrder
    fun receiveCar(carId: Long, rid: Long)
    fun sendCar(carId: Long, groupCode: String, rid: Long, projectId: Long, autoScheduleType: AutoScheduleType, sendType: SendType, carOtherType: CarOrderType, scheduleDetail: ProjectScheduleDetail?)
    fun sendCarDetail(carId: Long, groupCode: String, rid: Long, projectId: Long, autoScheduleType: AutoScheduleType, sendType: SendType, carOtherType: CarOrderType, carOrder: CarOrder?, scheduleDetail: ProjectScheduleDetail?)
    fun queryPage(
            current: Int?,
            pageSize: Int?,
            projectId: Long?,
            managerId: Long?
    ): Page<CarOrder>

    fun queryPage(
            current: Int?,
            pageSize: Int?,
            projectId: Long?,
            carCode: String?,
            startTime: Date?,
            endTime: Date
    ): Page<CarOrder>

    fun sendMqZero(carUid: String, rid: Long, sendType: SendType, projectId: Long)

    fun init(carOrder: CarOrder): CarOrder
}
