package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectCheckLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ProjectCheckLogRepository extends JpaRepository<ProjectCheckLog, Long>, JpaSpecificationExecutor<ProjectCheckLog> {

    @Query(nativeQuery = true, value = "select car_code, count(*) as count from project_check_log where projectid = ?1 " +
            " and time_check >= ?2 and time_check <= ?3 group by car_code")
    List<Map> getCheckCountByProjectIDAndTimeCheck(Long projectId, Date startTime, Date endTime);
}
