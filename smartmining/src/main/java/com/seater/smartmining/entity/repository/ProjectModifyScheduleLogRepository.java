package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectModifyScheduleLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/7/26 0026 10:44
 */
public interface ProjectModifyScheduleLogRepository extends JpaRepository<ProjectModifyScheduleLog,Long>, JpaSpecificationExecutor<ProjectModifyScheduleLog> {

    @Query(nativeQuery = true, value = "select * from project_modify_schedule_log" +
            " where project_id = ?1 and datediff(date_identification, ?2) = 0 and shift = ?3 order by modify_time desc")
    List<ProjectModifyScheduleLog> getAllByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift);
}
