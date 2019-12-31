package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectDayReportHistory;
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
 * @Date 2019/5/7 0007 15:55
 */
public interface ProjectDayReportHistoryRepository extends JpaRepository<ProjectDayReportHistory, Long>, JpaSpecificationExecutor<ProjectDayReportHistory> {

    @Query(nativeQuery = true, value = "select * from project_day_report_history where project_id = ?1 and datediff(report_date, ?2) = 0")
    ProjectDayReportHistory getAllByProjectIdAndReportDate(Long projectId, Date reportDate);

    @Query(nativeQuery = true, value = "delete from project_day_report_history where project_id = ?1 and datediff(report_date, ?2) = 0")
    @Modifying
    @Transactional
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate);
}
