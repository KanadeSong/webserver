package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectScheduleLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/7/23 0023 10:05
 */
public interface ProjectScheduleLogRepository extends JpaRepository<ProjectScheduleLog, Long>, JpaSpecificationExecutor<ProjectScheduleLog> {
}
