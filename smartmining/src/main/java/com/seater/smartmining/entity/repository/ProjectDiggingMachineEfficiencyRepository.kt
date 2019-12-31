package com.seater.smartmining.entity.repository

import com.seater.smartmining.entity.ProjectDiggingMachineEfficiency
import com.seater.smartmining.entity.ProjectScheduleDetail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface ProjectDiggingMachineEfficiencyRepository : JpaRepository<ProjectDiggingMachineEfficiency, Long>, JpaSpecificationExecutor<ProjectDiggingMachineEfficiency> {
}
