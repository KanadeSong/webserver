package com.seater.smartmining.dao

import com.seater.smartmining.entity.ProjectDiggingMachineEfficiency
import com.seater.smartmining.entity.ProjectScheduleDetail
import com.sytech.user.helpers.defaultPageSize
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.querydsl.QPageRequest

interface ProjectDiggingMachineEfficiencyDaoI {
    fun get(id: Long): ProjectDiggingMachineEfficiency?
    fun delete(id: Long)
    fun delete(ids: List<Long>)
    fun save(catalog: ProjectDiggingMachineEfficiency): ProjectDiggingMachineEfficiency
    fun query(spec: Specification<ProjectDiggingMachineEfficiency>? = null, pageable: Pageable = QPageRequest(0, defaultPageSize)): Page<ProjectDiggingMachineEfficiency>
}
