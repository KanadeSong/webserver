package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectCarCostAccounting;
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
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/2/22 0022 9:35
 */
public interface ProjectCarCostAccountingRepository extends JpaRepository<ProjectCarCostAccounting, Long>, JpaSpecificationExecutor<ProjectCarCostAccounting> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_car_cost_accounting where project_id = ?1 and datediff(report_date, ?2) = 0")
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate);

    @Query(nativeQuery = true, value = "select * from project_car_cost_accounting where project_id = ?1 and datediff(report_date, ?2) = 0 order by statistics_type")
    List<ProjectCarCostAccounting> getAllByProjectIdAndReportDate(Long projectId, Date reportDate);

    @Query(nativeQuery = true, value = "select * from project_car_cost_accounting where project_id = ?1 and report_date >= ?2 and report_date <= ?3")
    List<ProjectCarCostAccounting> getAllByProjectIdAndTime(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select sum(amount) as amount, sum(amount_by_fill) as amountByFill, sum(fill_count) as fillCount, sum(total_count) as totalCount, sum(total_cubic) as totalCubic, report_date from project_car_cost_accounting" +
            " where project_id = ?1 and report_date >= ?2 and report_date <= ?3 and statistics_type = 1" +
            " group by report_date")
    List<Map> getCarAmountReport(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select sum(amount) as amount, sum(amount_by_fill) as amountByFill, sum(fill_count) as fillCount, sum(total_count) as totalCount, sum(total_cubic) as totalCubic,date_format(report_date, '%Y-%m') as report_date from project_car_cost_accounting" +
            " where project_id = ?1 and report_date >= ?2 and report_date <= ?3 and statistics_type = 1 group by date_format(report_date, '%Y-%m')")
    List<Map> getCarAmountReportMonth(Long projectId, Date startTime, Date endTime);

    @Query("select max(reportDate) from ProjectCarCostAccounting")
    List<Date> getMaxReportDate();

    @Query(nativeQuery = true, value = "select sum(amount) as amount, sum(amount_by_fill) as amount_by_fill from project_car_cost_accounting where project_id = ?1 and report_date >= ?2 and report_date <= ?3 and statistics_type = 1")
    List<Map> getHistoryAmount(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select sum(amount) as amount, sum(amount_by_fill) as amount_by_fill from project_car_cost_accounting where project_id = ?1 and report_date <= ?2 " +
            " and statistics_type = 1")
    List<Map> getHistoryAmountHistory(Long projectId, Date date);

    @Query(nativeQuery = true, value = "select sum(amount) as amount, sum(amount_by_fill) as amount_by_fill, report_date from project_car_cost_accounting" +
            " where project_id = ?1 and report_date >= ?2 and report_date <= ?3 and statistics_type = 1 group by report_date")
    List<Map> getHistoryFillAmountAndAmount(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select sum(amount) as amount, sum(amount_by_fill) as amount_by_fill, date_format(report_date, '%Y-%m') as report_date from project_car_cost_accounting" +
            " where project_id = ?1 and report_date >= ?2 and report_date <= ?3 and statistics_type = 1 group by date_format(report_date, '%Y-%m')")
    List<Map> getHistoryFillAmountAndAmountMonth(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select sum(amount) as amount, sum(amount_by_fill) as amount_by_fill, report_date from project_car_cost_accounting" +
            " where project_id = ?1 and report_date <= ?2 and statistics_type = 1")
    List<Map> getHistoryFillAmountAndAmountHistory(Long projectId, Date date);

    @Query(nativeQuery = true, value = "select sum(total_count) as totalCount, sum(total_cubic) as totalCubic, sum(fill_count) as fillCount," +
            " sum(amount_by_fill) as amountByFill, sum(amount) as amount from project_car_cost_accounting where project_id = ?1 and statistics_type = 1" +
            " and report_date >= ?2 and report_date <= ?3")
    Map getAllByProjectIdAndReportDate(Long projectId, Date startTime, Date endTime);
}
