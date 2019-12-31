package com.seater.smartmining.service

import com.seater.smartmining.entity.ProjectDiggingMachineEfficiency
import com.sytech.user.helpers.defaultPageSize
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.querydsl.QPageRequest

interface ProjectDiggingMachineEfficiencyServiceI {
    fun get(id: Long?): ProjectDiggingMachineEfficiency?
    fun save(projectDiggingMachineEfficiency: ProjectDiggingMachineEfficiency): ProjectDiggingMachineEfficiency
    fun delete(id: Long)
    fun delete(ids: List<Long>)

    fun query(spec: Specification<ProjectDiggingMachineEfficiency>? = null, pageable: Pageable = QPageRequest(0, defaultPageSize)): Page<ProjectDiggingMachineEfficiency>

    /**
     *
     */


    fun queryPage(
            current: Int?,
            pageSize: Int?,
            projectDiggingMachineId: Long?,
            groupCode: String?,
            projectId: Long?
    ): Page<ProjectDiggingMachineEfficiency>

}
