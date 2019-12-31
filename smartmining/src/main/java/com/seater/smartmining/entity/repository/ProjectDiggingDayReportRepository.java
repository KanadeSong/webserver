package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectDiggingDayReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/2/12 0012 16:27
 */
public interface ProjectDiggingDayReportRepository extends JpaRepository<ProjectDiggingDayReport, Long>, JpaSpecificationExecutor<ProjectDiggingDayReport> {

    List<ProjectDiggingDayReport> getByTotalId(Long totalId);

    /*@Query(nativeQuery = true, value = "select machine_id,machine_code,machine_name,owner_id, owner_name,sum(total_work_timer) as total_work_timer,sum(total_time_by_timer) as total_time_by_timer, " +
            " price_by_timer,sum(amount_by_timer) as amount_by_timer,sum(total_time_by_cubic) as total_time_by_cubic, " +
            " sum(car_total_count_by_cubic) as car_total_count_by_cubic,sum(total_count_by_cubic) as total_count_by_cubic, " +
            " sum(total_amount_by_cubic) as total_amount_by_cubic,sum(total_grand_fill) as total_grand_fill, " +
            " sum(total_amount_by_fill) as total_amount_by_fill,sum(should_pay_amount) as should_pay_amount, " +
            " sum(total_amount) as total_amount from project_digging_day_report " +
            " where project_id = ?1 and report_date >= ?2 and report_date <= ?3 " +
            " group by machine_id,machine_code,machine_name,owner_id,owner_name,price_by_timer order by machine_id")
    List<Map> getMonthReportByProjectIdAndReportDate(Long projectId, Date startDate, Date endDate);*/

    @Query(nativeQuery = true, value = "select machine_id,machine_code,machine_name,owner_id, owner_name,sum(total_work_timer) as total_work_timer,sum(total_time_by_timer) as total_time_by_timer, " +
            " price_by_timer,sum(amount_by_timer) as amount_by_timer,sum(total_time_by_cubic) as total_time_by_cubic, " +
            " sum(car_total_count_by_cubic) as car_total_count_by_cubic,sum(total_count_by_cubic) as total_count_by_cubic, " +
            " sum(total_amount_by_cubic) as total_amount_by_cubic,sum(total_grand_fill) as total_grand_fill, " +
            " sum(total_amount_by_fill) as total_amount_by_fill,sum(should_pay_amount) as should_pay_amount, " +
            " sum(total_amount) as total_amount, sum(total_grand_fill_by_timer) as total_grand_fill_by_timer, sum(total_grand_fill_by_cubic) as total_grand_fill_by_cubic," +
            " sum(total_amount_by_fill_by_timer) as total_amount_by_fill_by_timer, sum(total_amount_by_fill_by_cubic) as total_amount_by_fill_by_cubic from project_digging_day_report " +
            " where project_id = ?1 and report_date >= ?2 and report_date <= ?3 " +
            " group by machine_id,machine_code,machine_name,owner_id,owner_name,price_by_timer order by machine_id")
    List<Map> getMonthReportByProjectIdAndReportDate(Long projectId, Date startDate, Date endDate);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_digging_day_report where project_id = ?1 and datediff(report_date, ?2) = 0")
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate);

    @Query(nativeQuery = true, value = "select sum(total_count_by_timer) as total_count_by_timer,sum(cubic_count_by_timer) as cubic_count_by_timer," +
            " sum(total_time_by_timer) as total_time_by_timer,sum(amount_by_timer) as amount_by_timer," +
            " sum(total_time_by_cubic) as total_time_by_cubic,sum(car_total_count_by_cubic) as car_total_count_by_cubic," +
            " sum(total_count_by_cubic) as total_count_by_cubic,sum(total_amount_by_cubic) as total_amount_by_cubic" +
            " from project_digging_day_report where project_id = ?1 and datediff(report_date, ?2) = 0")
    Map getTotalInfoByProjectIdAndTime(Long projectId, Date reportDate);

    @Query(nativeQuery = true, value = "select sum(grand_total_count_by_timer) as total_count_by_timer,sum(grand_cubic_count_by_timer) as cubic_count_by_timer," +
            " sum(grand_time_by_timer) as total_time_by_timer,sum(grand_amount_by_timer) as amount_by_timer," +
            " sum(grand_time_by_cubic) as total_time_by_cubic,sum(count_cars_by_cubic) as car_total_count_by_cubic," +
            " sum(count_cubic) as total_count_by_cubic,sum(count_amount_by_cubic) as total_amount_by_cubic from project_digging_day_report" +
            " where project_id = ?1 and datediff(report_date, ?2) = 0")
    Map getGrandInfoByProjectIdAndTime(Long projectId, Date reportDate);

    @Query(nativeQuery = true, value = "select sum(total_count_by_timer) as total_count_by_timer,sum(cubic_count_by_timer) as cubic_count_by_timer," +
            " sum(total_time_by_timer) as total_time_by_timer,sum(amount_by_timer) as amount_by_timer," +
            " sum(total_time_by_cubic) as total_time_by_cubic,sum(car_total_count_by_cubic) as car_total_count_by_cubic," +
            " sum(total_count_by_cubic) as total_count_by_cubic,sum(total_amount_by_cubic) as total_amount_by_cubic" +
            " from project_digging_day_report where project_id = ?1")
    Map getHistoryInfoByProjectId(Long projectId);

    @Transactional
    @Modifying
    @Query("update ProjectDiggingDayReport set deductionTimeByDay = ?2, deductionTimeByNight = ?3 where id = ?1")
    void setDeductionTimeByDayAndDeductionTimeByNightOrderById(Long id, BigDecimal deductionTimeByDay, BigDecimal deductionTimeByNight);

    @Query(nativeQuery = true, value = "select sum(total_time_by_timer) as totalTimeByTimer,price_by_timer,sum(amount_by_timer) as amountByTimer,sum(total_time_by_cubic) as totalTimeByCubic," +
            " sum(total_amount_by_cubic) as totalAmountByCubic,sum(total_amount) as totalAmount,sum(total_grand_fill) as totalGrandFill,sum(total_amount_by_fill) as totalAmountByFill,report_date" +
            " from project_digging_day_report where project_id = ?1 and report_date >= ?2 and report_date <= ?3 and machine_id =?4 group by machine_code,price_by_timer,report_date order by report_date")
    List<Map> getCubicDetailByProjectIdAndReportDateAndMachineId(Long projectId, Date startTime, Date endTime, Long machineId);

    @Query(nativeQuery = true, value = "select * from project_digging_day_report where project_id = ?1 and machine_id = ?2 and datediff(report_date, ?3) = 0")
    List<ProjectDiggingDayReport> getAllByProjectIdAndMachineIdAndReportDate(Long projectId, Long machineId, Date reportDate);
}
