package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectDiggingDayReportTotal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/2/15 0015 15:30
 */
public interface ProjectDiggingDayReportTotalRepository extends JpaRepository<ProjectDiggingDayReportTotal,Long>, JpaSpecificationExecutor<ProjectDiggingDayReportTotal> {

    @Query(nativeQuery = true, value = "select * from project_digging_day_report_total where project_id = ?1 and datediff(report_date, ?2) = 0")
    List<ProjectDiggingDayReportTotal> getByProjectIdAndReportDate(Long projectId, Date reportDate);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_digging_day_report_total where project_id = ?1 and datediff(report_date, ?2) = 0")
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate);

    @Query(nativeQuery = true, value = "select * from project_digging_day_report_total " +
            " where project_id = ?1 and report_date >= ?2 and report_date <= ?3")
    List<ProjectDiggingDayReportTotal> getAllByProjectIdAndTime(Long projectId, Date startTime, Date endTime);
}
