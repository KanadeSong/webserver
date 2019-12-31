package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectDiggingMonthReportTotal;
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
 * @Date 2019/2/16 0016 13:28
 */
public interface ProjectDiggingMonthReportTotalRepository extends JpaRepository<ProjectDiggingMonthReportTotal,Long>, JpaSpecificationExecutor<ProjectDiggingMonthReportTotal> {

    @Query(nativeQuery = true,value = "select * from project_digging_month_report_total where project_id = ?1 and datediff(report_date, ?2) = 0")
    List<ProjectDiggingMonthReportTotal> getByProjectIdAndReportDate(Long projectId, Date reportDate);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_digging_month_report_total where project_id = ?1 and datediff(report_date, ?2) = 0")
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate);

    @Transactional
    @Modifying
    @Query("update ProjectDiggingMonthReportTotal set deduction = ?2, subsidyAmount = ?3 where id = ?1")
    void setDeductionAndSubsidyAmount(Long id, Long deduction, Long subsidyAmount);
}
