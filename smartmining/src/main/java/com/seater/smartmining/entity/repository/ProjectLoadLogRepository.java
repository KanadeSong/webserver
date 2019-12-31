package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectLoadLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ProjectLoadLogRepository extends JpaRepository<ProjectLoadLog, Long>, JpaSpecificationExecutor<ProjectLoadLog> {

    @Query("select max(timeLoad) from ProjectLoadLog where excavatCurrent = ?1 and valid = true")
    List<Date> getMaxUnloadDateByCarCode(String carCode);

    @Query(nativeQuery = true, value = "select car_code, count(*) as count from project_load_log where projectid = ?1" +
            " and time_load >= ?2 and time_load <= ?3 and valid = true group by car_code")
    List<Map> getMachineCountByProjectIdAndTime(Long projectId, Date startTime, Date endTime);
}
