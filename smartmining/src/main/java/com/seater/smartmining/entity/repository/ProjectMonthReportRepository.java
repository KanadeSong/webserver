package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectMonthReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/2/19 0019 10:39
 */
public interface ProjectMonthReportRepository extends JpaRepository<ProjectMonthReport,Long>, JpaSpecificationExecutor<ProjectMonthReport> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_month_report where project_id = ?1 and datediff(report_date, ?2) = 0")
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDay);

    List<ProjectMonthReport> getByTotalId(Long totalId);

    @Transactional
    @Modifying
    @Query("update ProjectMonthReport set deduction = ?2, subsidyAmount = ?3 where id = ?1")
    void setDeductionAndSubsidyAmount(Long id,Long deduction,Long subsidyAmount);

    List<ProjectMonthReport> getByTotalIdAndCarIdIn(Long totalId, List<Long> carIds);

    @Query(nativeQuery = true, value = "select * from project_month_report where project_id = ?1 and car_id = ?2 and datediff(report_date, ?3) = 0")
    ProjectMonthReport getAllByProjectIdAndCarIdAndReportDate(Long projectId, Long carId, Date reportDate);
}
