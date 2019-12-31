package com.seater.smartmining.service

import com.seater.smartmining.entity.CarText
import com.sytech.user.helpers.defaultPageSize
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification

interface CarTextServiceI {
    fun get(id: Long?): CarText?
    fun save(CarText: CarText): CarText
    fun add(CarText: CarText): CarText
    fun update(CarText: CarText, old: CarText): CarText
    fun delete(id: Long)
    fun delete(ids: List<Long>)
    fun query(spec: Specification<CarText>? = null, pageable: Pageable = PageRequest.of(0, defaultPageSize)): Page<CarText>
}
