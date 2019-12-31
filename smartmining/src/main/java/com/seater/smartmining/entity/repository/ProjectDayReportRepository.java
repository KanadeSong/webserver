package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectDayReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ProjectDayReportRepository extends JpaRepository<ProjectDayReport, Long>, JpaSpecificationExecutor<ProjectDayReport> {
    @Query(nativeQuery = true, value = "select * from project_day_report where project_id = ?1 and datediff(report_date, ?2) = 0")
    ProjectDayReport getByProjectIdAndReportDate(Long projectId, Date reportDate);

    @Query(nativeQuery = true, value = "select * from project_day_report where project_id = ?1 and datediff(create_date, ?2) = 0 order by report_date desc")
    List<ProjectDayReport> getByProjectIdAndCreateDate(Long projectId, Date createDate);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_day_report where project_id = ?1 and datediff(report_date, ?2) = 0")
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate);

    @Query(nativeQuery = true, value = "select sum(on_duty_count) as on_duty_count from project_day_report where project_id = ?1 and report_date >= ?2 and report_date <= ?3")
    Map getOnDutyCountByProjectIdAndReportDate(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select * from project_day_report where project_id = ?1 and report_date >= ?2 and report_date <= ?3")
    List<ProjectDayReport> getSettlementDetailByProjectIdAndTime(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select sum(total_cubic) as totalCubic, sum(total_count) as totalCount, sum(total_fill) as totalFill, sum(mileage) as mileage, sum(total_amount) as totalAmount, sum(on_duty_count) as onDutyCount, sum(total_amount_fill) as totalAmountFill, report_date from project_day_report" +
            " where project_id = ?1 and report_date >= ?2 and report_date <= ?3 group by report_date")
    List<Map> getAvgCarInfo(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select sum(total_cubic) as totalCubic, sum(total_count) as totalCount, sum(total_fill) as totalFill, sum(mileage) as mileage, sum(total_amount) as totalAmount, sum(on_duty_count) as onDutyCount, sum(total_amount_fill) as totalAmountFill, date_format(report_date, '%Y-%m') as report_date from project_day_report" +
            " where project_id = ?1 and report_date >= ?2 and report_date <= ?3 group by date_format(report_date, '%Y-%m')")
    List<Map> getAvgCarInfoMonth(Long projectId, Date startTime, Date endTime);
}
