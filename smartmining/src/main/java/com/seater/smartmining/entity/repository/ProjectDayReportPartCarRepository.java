package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectDayReportPartCar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ProjectDayReportPartCarRepository  extends JpaRepository<ProjectDayReportPartCar, Long>, JpaSpecificationExecutor<ProjectDayReportPartCar> {
    List<ProjectDayReportPartCar> getByReportIdOrderByCarCode(Long reportId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_day_report_part_car where project_id = ?1 and datediff(report_date, ?2) = 0")
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate);

    @Query(nativeQuery = true, value = "select * from project_day_report_part_car where project_id = ?1 and datediff(report_date, ?2) = 0")
    ProjectDayReportPartCar getByProjectIdAndReportDate(Long projectId, Date reportDate);

    @Query(nativeQuery = true, value = "select sum(total_count) as total_count,sum(total_cubic) as total_cubic,sum(total_fill) as total_fill," +
            " sum(total_amount_fill) as total_amount_fill,sum(total_amount) as total_amount from project_day_report_part_car" +
            " where project_id = ?1 and datediff(report_date, ?2) = 0")
    Map getTotalInfoByProjectIdAndReportDate(Long projectId, Date reportDate);

    @Query(nativeQuery = true, value = "select sum(grand_total_count) as total_count,sum(grand_total_cubic) as total_cubic," +
            " sum(grand_total_fill) as total_fill,sum(grand_total_amount_fill) as total_amount_fill," +
            " sum(grand_total_amount) as total_amount from project_day_report_part_car" +
            " where project_id = ?1 and datediff(report_date, ?2) = 0")
    Map getGrandInfoByProjectIdAndReportDate(Long projectId, Date reportDate);

    @Query(nativeQuery = true, value = "select sum(grand_total_count) as total_count,sum(grand_total_cubic) as total_cubic," +
            " sum(grand_total_fill) as total_fill,sum(grand_total_amount_fill) as total_amount_fill," +
            " sum(grand_total_amount) as total_amount from project_day_report_part_car where project_id = ?1")
    Map getHistoryInfoByProjectId(Long projectId);

    @Query(nativeQuery = true, value = "select car_id,car_code,car_owner_name,sum(total_count) as total_count,sum(total_cubic) as total_cubic," +
            " sum(total_amount) as total_amount,sum(total_fill) as total_fill,sum(total_amount_fill) as total_amount_fill," +
            " sum(payable) as payable,sum(mileage) as mileage from project_day_report_part_car" +
            " where project_id = ?1 and report_date >= ?2 and report_date <= ?3" +
            " group by car_id,car_code,car_owner_name" +
            " order by car_id ")
    List<Map> getMonthReportByProjectIdAndReportDate(Long projectId, String startTime, String endTime);
    /*List<Map> getMonthReportByProjectIdAndReportDate(Long projectId, Date startTime, Date endTime);*/

    @Query(nativeQuery = true, value = "select distinct car_id from project_day_report_part_car " +
            "where project_id = ?1 and report_date >= ?2 and report_date <= ?3")
    List<Map> getMonthCarCountByProjectIdAndReportDate(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery =true, value = "select sum(total_fill) as totalFill,sum(total_count) as totalCount,sum(total_amount) as totalAmount," +
            " sum(total_amount_fill) as totalAmountFill from project_day_report_part_car" +
            " where project_id = ?1 and report_id = ?2 and car_id = ?3")
    Map getSettlementDetailByProjectIdAndReportIdAndCarId(Long projectId, Long reportId, Long carId);
}
