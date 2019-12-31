package com.seater.smartmining.dao

import com.seater.smartmining.entity.CarText
import com.sytech.user.helpers.defaultPageSize
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification

interface CarTextDaoI {
    fun get(id: Long): CarText?
    fun delete(id: Long)
    fun delete(ids: List<Long>)
    fun save(CarText: CarText): CarText
    fun query(spec: Specification<CarText>? = null, pageable: Pageable = PageRequest.of(0, defaultPageSize)): Page<CarText>

    fun getMaxOrderNumber(): Long
}
