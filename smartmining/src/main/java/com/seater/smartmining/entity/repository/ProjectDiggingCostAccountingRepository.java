package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectDiggingCostAccounting;
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
 * @Date 2019/2/21 0021 9:01
 */
public interface ProjectDiggingCostAccountingRepository extends JpaRepository<ProjectDiggingCostAccounting, Long>, JpaSpecificationExecutor<ProjectDiggingCostAccounting> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_digging_cost_accounting where project_id = ?1 and datediff(report_date, ?2) = 0")
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate);

    @Query(nativeQuery = true, value = "select * from project_digging_cost_accounting where project_id = ?1 and datediff(report_date, ?2) = 0 order by statistics_type")
    List<ProjectDiggingCostAccounting> getAllByProjectIdAndReportDate(Long projectId, Date reportDate);

    @Query(nativeQuery = true, value = "select sum(amount_by_timer) as totalAmountByTimer, sum(total_amount_by_cubic) as totalAmountByCubic, " +
            " sum(total_count_by_timer) as totalCountByTimer, sum(total_count_by_cubic) as totalCountByCubic, sum(total_cubic_by_timer) as totalCubicByTimer," +
            " sum(total_cubic_by_cubic) as totalCubicByCubic, sum(amount_by_fill_by_timer) as amountByFillByTimer, sum(amount_by_fill_by_cubic) as amountByFillByCubic, " +
            " sum(fill_count_by_timer) as fillCountByTimer, sum(fill_count_by_cubic) as fillCountByCubic,sum(work_time_by_timer) as workTimeByTimer, " +
            " sum(work_time_by_cubic) as workTimeByCubic, report_date from project_digging_cost_accounting " +
            " where project_id = ?1 and report_date >= ?2 and report_date <= ?3" +
            " and statistics_type = 1 group by report_date order by report_date")
    List<Map> getAllByProjectIdAndTime(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select sum(amount_by_timer) as totalAmountByTimer, sum(total_amount_by_cubic) as totalAmountByCubic, " +
            " sum(total_count_by_timer) as totalCountByTimer, sum(total_count_by_cubic) as totalCountByCubic, sum(total_cubic_by_timer) as totalCubicByTimer, " +
            " sum(total_cubic_by_cubic) as totalCubicByCubic, sum(amount_by_fill_by_timer) as amountByFillByTimer, sum(amount_by_fill_by_cubic) as amountByFillByCubic, " +
            " sum(fill_count_by_timer) as fillCountByTimer, sum(fill_count_by_cubic) as fillCountByCubic,sum(work_time_by_timer) as workTimeByTimer," +
            " sum(work_time_by_cubic) as workTimeByCubic, date_format(report_date, '%Y-%m') as reportDate from project_digging_cost_accounting" +
            " where project_id = ?1 and report_date >= ?2 and report_date <= ?3 and statistics_type = 1 group by date_format(report_date, '%Y-%m') order by date_format(report_date, '%Y-%m')")
    List<Map> getAllByProjectIdAndTimeMonth(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select sum(total_amount_by_timer) as totalAmountByTimer, sum(total_amount_by_cubic) as totalAmountByCubic, " +
            " sum(total_count_by_timer) as totalCountByTimer, sum(total_count_by_cubic) as totalCountByCubic, sum(total_cubic_by_timer) as totalCubicByTimer,  " +
            " sum(total_cubic_by_cubic) as totalCubicByCubic, sum(amount_by_fill_by_timer) as amountByFillByTimer, sum(amount_by_fill_by_cubic) as amountByFillByCubic, " +
            " sum(fill_count_by_timer) as fillCountByTimer, sum(fill_count_by_cubic) as fillCountByCubic,sum(work_time_by_timer) as workTimeByTimer," +
            " sum(work_time_by_cubic) as workTimeByCubic, date_format(report_date, '%Y-%m') as reportDate from project_digging_cost_accounting" +
            " where project_id = ?1 and report_date <= ?2 and statistics_type = 1 group by date_format(report_date, '%Y-%m') order by date_format(report_date, '%Y-%m')")
    List<Map> getAllByProjectIdAndTimeHistory(Long projectId, Date endTime);

    @Query("select max(reportDate) from ProjectDiggingCostAccounting")
    List<Date> getMaxReportDate();

    @Query(nativeQuery = true, value = "select sum(total_amount) as total_amount, sum(amount_by_fill_by_timer) as amount_by_fill_by_timer, sum(amount_by_fill_by_cubic) as amount_by_fill_by_cubic from project_digging_cost_accounting where project_id = ?1" +
            " and report_date >= ?2 and report_date <= ?3 and statistics_type = 1")
    List<Map> getHistoryAmount(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select sum(total_amount) as total_amount, sum(amount_by_fill_by_timer) as amount_by_fill_by_timer, sum(amount_by_fill_by_cubic) as amount_by_fill_by_cubic from project_digging_cost_accounting where project_id = ?1" +
            " and report_date <= ?2 and statistics_type = 3")
    List<Map> getHistoryAmountHistory(Long projectId, Date endDate);

    @Query(nativeQuery = true, value = "select sum(total_amount) as total_amount, sum(amount_by_fill_by_timer) as amount_by_fill_by_timer, sum(amount_by_fill_by_cubic) as amount_by_fill_by_cubic, report_date from project_digging_cost_accounting" +
            " where project_id = ?1 and report_date >= ?2 and report_date <= ?3 and statistics_type = 1 group by report_date")
    List<Map> getHistoryFillAmountAndAmount(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select sum(total_amount) as total_amount, sum(amount_by_fill_by_timer) as amount_by_fill_by_timer, sum(amount_by_fill_by_cubic) as amount_by_fill_by_cubic, date_format(report_date, '%Y-%m') as report_date from project_digging_cost_accounting" +
            " where project_id = ?1 and report_date >= ?2 and report_date <= ?3 and statistics_type = 1 group by date_format(report_date, '%Y-%m')")
    List<Map> getHistoryFillAmountAndAmountMonth(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select sum(total_count_by_timer) as totalCountByTimer, sum(total_cubic_by_timer) as totalCubicByTimer, sum(work_time_by_timer) as workTimeByTimer, " +
            " sum(amount_by_timer) as amountByTimer, sum(fill_count_by_timer) as fillCountByTimer, sum(amount_by_fill_by_timer) as amountByFillByTimer," +
            " sum(total_amount_by_timer) as totalAmountByTimer, sum(work_time_by_single_hook) as workTimeBySingleHook, sum(amount_by_single_hook) as amountBySingleHook," +
            " sum(fill_count_by_single_hook) as fillCountBySingleHook, sum(amount_by_fill_by_single_hook) as amountByFillBySingleHook, sum(total_amount_by_single_hook) as totalAmountBySingleHook," +
            " sum(work_time_by_gun_hammer) as workTimeByGunHammer, sum(amount_by_gun_hammer) as amountByGunHammer, sum(fill_count_by_gun_hammer) as fillCountByGunHammer," +
            " sum(amount_by_fill_by_gun_hammer) as amountByFillByGunHammer, sum(total_amount_by_gun_hammer) as totalAmountByGunHammer, sum(work_time_by_cubic) as workTimeByCubic," +
            " sum(total_count_by_cubic) as totalCountByCubic, sum(total_cubic_by_cubic) as totalCubicByCubic, sum(fill_count_by_cubic) as fillCountByCubic," +
            " sum(amount_by_fill_by_cubic) as amountByFillByCubic, sum(total_amount_by_cubic) as totalAmountByCubic, sum(total_amount) as totalAmount," +
            " sum(fill_count_by_total) as fillCountByTotal, sum(amount_by_fill_by_total) as amountByFillByTotal from project_digging_cost_accounting" +
            " where project_id = ?1 and statistics_type = 1 and report_date >= ?1 and report_date <= ?2")
    Map getAllByProjectIdAndReportDate(Long projectId, Date startTime, Date endTime);
}
