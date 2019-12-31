package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectAppStatisticsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/4/11 0011 16:39
 */
public interface ProjectAppStatisticsLogRepository extends JpaRepository<ProjectAppStatisticsLog, Long>, JpaSpecificationExecutor<ProjectAppStatisticsLog> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_app_statistics_log where project_id = ?1 and datediff(report_date, ?2) = 0")
    void deleteByProjectAndReportDate(Long projectId, Date reportDate);

    @Query(nativeQuery = true, value = "select * from project_app_statistics_log where project_id = ?1 and datediff(report_date, ?2) = 0 order by id desc")
    List<ProjectAppStatisticsLog> getAllByProjectIdAndReportDate(Long projectId, Date reportDate);
}
