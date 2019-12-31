package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectDiggingDayReportHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/6/11 0011 16:29
 */
public interface ProjectDiggingDayReportHistoryRepository extends JpaRepository<ProjectDiggingDayReportHistory, Long>, JpaSpecificationExecutor<ProjectDiggingDayReportHistory> {
    @Query(nativeQuery = true, value = "select * from project_digging_day_report_history where project_id = ?1 and datediff(report_date, ?2) = 0")
    ProjectDiggingDayReportHistory getAllByProjectIdAndReportDate(Long projectId, Date reportDate);

    @Query(nativeQuery = true, value = "delete from project_digging_day_report_history where project_id = ?1 and datediff(report_date, ?2) = 0")
    @Modifying
    @Transactional
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate);
}
