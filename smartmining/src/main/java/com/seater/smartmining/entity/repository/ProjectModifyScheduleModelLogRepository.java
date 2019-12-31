package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectModifyScheduleModelLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/15 0015 14:13
 */
public interface ProjectModifyScheduleModelLogRepository extends JpaRepository<ProjectModifyScheduleModelLog,Long>, JpaSpecificationExecutor<ProjectModifyScheduleModelLog> {

}
