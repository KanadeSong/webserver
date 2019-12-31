package com.seater.smartmining.controller

import com.seater.smartmining.service.ProjectDiggingMachineEfficiencyServiceI
import com.seater.smartmining.service.ProjectDiggingMachineServiceI
import com.seater.smartmining.service.ProjectScheduleDetailServiceI
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Catalog
 */
@RestController
@javax.transaction.Transactional(rollbackOn = [Exception::class])
@RequestMapping("/api/projectScheduleAuto")
class ProjectScheduleAutoController(
        val projectScheduleDetailServiceI: ProjectScheduleDetailServiceI,
        val projectDiggingMachineEfficiencyServiceI: ProjectDiggingMachineEfficiencyServiceI
) {

    /**
     * @Description 查询 ProjectScheduleDetail
     * @url /api/projectScheduleAuto/queryProjectScheduleDetail
     * @return <code></code>
     */
    @RequestMapping("/queryProjectScheduleDetail")
    fun queryProjectScheduleDetail(
            current: Int?,
            pageSize: Int?,
            groupCode: String?,
            @RequestHeader(value="projectId") projectId: Long?
    ): Any? {
        val rt = projectScheduleDetailServiceI.queryPage(
                current = current,
                pageSize = pageSize,
                groupCode = groupCode,
                projectId = projectId,
                disable = false,
                showReady = false,
                managerId = null
        )
        return mapOf(
                "content" to rt.content.map{
                    mapOf(
                            "it" to it,
                            "other" to null
                    )
                },
                "totalElements" to rt.totalElements
        )
    }

    /**
     * @Description 查询 ProjectDiggingMachineEfficiency
     * @url /api/projectScheduleAuto/queryProjectDiggingMachineEfficiency
     * @return <code></code>
     */
    @RequestMapping("/queryProjectDiggingMachineEfficiency")
    fun queryProjectDiggingMachineEfficiency(
            current: Int?,
            pageSize: Int?,
            groupCode: String?,
            projectDiggingMachineId: Long?,
            @RequestHeader(value="projectId") projectId: Long?
    ): Any? {
        val rt = projectDiggingMachineEfficiencyServiceI.queryPage(
                current = current,
                pageSize = pageSize,
                groupCode = groupCode,
                projectDiggingMachineId = projectDiggingMachineId,
                projectId = projectId
        )
        return mapOf(
                "content" to rt.content.map{
                    mapOf(
                            "it" to it,
                            "other" to null
                    )
                },
                "totalElements" to rt.totalElements
        )
    }

    /**
     * @Description 重置
     * @url /api/projectScheduleAuto/reset
     * @return {"status":"success","message":""}
     */
    @RequestMapping("/reset")
    fun reset(
            groupCode: String,
            @RequestHeader(value="projectId") projectId: Long
    ): Any? {
        return projectScheduleDetailServiceI.reset(
                groupCode = groupCode,
                projectId = projectId
        )
    }
}