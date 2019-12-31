package com.seater.smartmining.entity.repository

import com.seater.smartmining.entity.CarOrder
import com.seater.smartmining.entity.CarOrderState
import com.seater.smartmining.entity.CarOrderType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import java.util.*

interface CarOrderRepository : JpaRepository<CarOrder, Long>, JpaSpecificationExecutor<CarOrder> {

    @Query("select max(orderNumber) from CarOrder")
    fun getMaxOrderNumber(): Long?

    fun findByCarIdAndCarOrderState(cardId: Long, carOrderState: CarOrderState): List<CarOrder>
    fun findByCarIdAndCarOrderStateNot(cardId: Long, carOrderState: CarOrderState): List<CarOrder>
    fun findByCarIdAndCarOrderTypeAndAndDetailUpdateTime(cardId: Long, carOrderType: CarOrderType, detailUpdateTime: Date): List<CarOrder>

    fun findBySendTimeBeforeAndRidIsNotNullAndCarOrderStateNot(sendTime: Date, carOrderState: CarOrderState): List<CarOrder>

    fun getById(id: Long): CarOrder?

    fun findByCarIdAndCreateTimeAfterOrderByIdDesc(cardId: Long, timeAfter: Date): List<CarOrder>
    fun findByCarIdAndCreateTimeAfterAndCarOrderStateOrderByIdDesc(cardId: Long, timeAfter: Date, carOrderState: CarOrderState): List<CarOrder>
    fun findByDiggingMachineIdAndCreateTimeAfterAndCarOrderStateOrderByIdDesc(diggingMachineId: Long, timeAfter: Date, carOrderState: CarOrderState): List<CarOrder>
}
