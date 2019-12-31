package com.seater.smartmining.dao

import com.seater.smartmining.entity.ProjectScheduleDetail
import com.sytech.user.helpers.defaultPageSize
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.querydsl.QPageRequest

interface ProjectScheduleDetailDaoI {
    fun get(id: Long): ProjectScheduleDetail?
    fun delete(id: Long)
    fun delete(ids: List<Long>)
    fun save(catalog: ProjectScheduleDetail): ProjectScheduleDetail
    fun query(spec: Specification<ProjectScheduleDetail>? = null, pageable: Pageable = QPageRequest(0, defaultPageSize)): Page<ProjectScheduleDetail>

    fun getByProjectCarIdAndProjectId(projectCarId: Long, projectId: Long): ProjectScheduleDetail?

    fun findByProjectDigIdInAndProjectId(projectDigIdList: List<Long>, projectId: Long): List<ProjectScheduleDetail>
    fun findByProjectSiteIdInAndProjectId(projectSiteIdList: List<Long>, projectId: Long): List<ProjectScheduleDetail>
}
