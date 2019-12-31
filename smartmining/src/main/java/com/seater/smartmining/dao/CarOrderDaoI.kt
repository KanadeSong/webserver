package com.seater.smartmining.dao

import com.seater.smartmining.entity.CarOrder
import com.sytech.user.helpers.defaultPageSize
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification

interface CarOrderDaoI {
    fun get(id: Long): CarOrder?
    fun delete(id: Long)
    fun delete(ids: List<Long>)
    fun save(CarOrder: CarOrder): CarOrder
    fun query(spec: Specification<CarOrder>? = null, pageable: Pageable = PageRequest.of(0, defaultPageSize)): Page<CarOrder>

    fun getMaxOrderNumber(): Long
}
