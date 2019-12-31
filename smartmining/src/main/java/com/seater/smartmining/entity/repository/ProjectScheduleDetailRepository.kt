package com.seater.smartmining.entity.repository

import com.seater.smartmining.entity.ProjectScheduleDetail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface ProjectScheduleDetailRepository : JpaRepository<ProjectScheduleDetail, Long>, JpaSpecificationExecutor<ProjectScheduleDetail> {
    fun findByProjectCarIdAndProjectIdOrderByIdDesc(projectCarId: Long, projectId: Long): List<ProjectScheduleDetail>
    fun findByProjectCarIdInAndProjectIdAndDisableIsFalse(projectCarIdList: List<Long>, projectId: Long): List<ProjectScheduleDetail>
    fun findByProjectDiggingMachineIdInAndProjectIdAndDisableIsFalse(projectDiggingMachineIdList: List<Long>, projectId: Long): List<ProjectScheduleDetail>
    fun findByProjectSlagSiteIdInAndProjectIdAndDisableIsFalse(projectSlagSiteIdList: List<Long>, projectId: Long): List<ProjectScheduleDetail>
    fun findByProjectCarIdInAndProjectId(projectCarIdList: List<Long>, projectId: Long): List<ProjectScheduleDetail>
    fun findByProjectDiggingMachineIdInAndProjectId(projectDiggingMachineIdList: List<Long>, projectId: Long): List<ProjectScheduleDetail>
    fun findByProjectSlagSiteIdInAndProjectId(projectSlagSiteIdList: List<Long>, projectId: Long): List<ProjectScheduleDetail>
    fun findByProjectCarIdAndProjectId(projectCarId: Long, projectId: Long): List<ProjectScheduleDetail>
}
