package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectMonthReportTotal;
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
 * @Date 2019/2/19 0019 10:49
 */
public interface ProjectMonthReportTotalRepository extends JpaRepository<ProjectMonthReportTotal,Long>, JpaSpecificationExecutor<ProjectMonthReportTotal> {

    @Query(nativeQuery = true, value = "select * from project_month_report_total where project_id = ?1 and datediff(report_date, ?2) = 0")
    List<ProjectMonthReportTotal> getByProjectIdAndReportDate(Long projectId, Date reportDate);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_month_report_total where project_id = ?1 and datediff(report_date, ?2) = 0")
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate);

    @Transactional
    @Modifying
    @Query("update ProjectMonthReportTotal set deduction = ?2, subsidyAmount = ?3 where id = ?1")
    void setDeductionAndSubsidyAmount(Long id, Long deduction, Long subsidyAmount);

}
