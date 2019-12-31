package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectWorkTimeByDiggingLog;
import com.seater.smartmining.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/23 0023 15:55
 */
public interface ProjectWorkTimeByDiggingLogRepository extends JpaRepository<ProjectWorkTimeByDiggingLog, Long>, JpaSpecificationExecutor<ProjectWorkTimeByDiggingLog> {

    @Query(nativeQuery = true, value = "select * from project_work_time_by_digging_log" +
            " where project_id = ?1 and datediff(date_identification, ?2) = 0 and shift = ?3 order by create_time desc")
    List<ProjectWorkTimeByDiggingLog> getAllByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift);
}
