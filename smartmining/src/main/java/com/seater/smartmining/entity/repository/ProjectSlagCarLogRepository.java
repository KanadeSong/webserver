package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectSlagCarLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/8/16 0016 16:55
 */
public interface ProjectSlagCarLogRepository extends JpaRepository<ProjectSlagCarLog, Long>, JpaSpecificationExecutor<ProjectSlagCarLog> {

    @Query(nativeQuery = true, value = "select car_code, count(*) as count from project_slag_car_log" +
            " where projectid = ?1 and time_load >= ?2 and time_load <= ?3 group by car_code")
    List<Map> getCarCountByProjectIDAndTime(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select * from project_slag_car_log where projectid = ?1 and car_code = ?2 and terminal_time = ?3")
    ProjectSlagCarLog getAllByProjectIDAndCarCodeAndTerminalTime(Long projectId, String carCode, Long terminalTime);

    @Query(nativeQuery = true, value = "select * from project_slag_car_log where projectid = ?1 and time_load >= ?2 and time_load <= ?3")
    List<ProjectSlagCarLog> getAllByProjectIDAndTimeDischarge(Long projectId, Date startTime, Date endTime);
}
