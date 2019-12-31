package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectDiggingDayReport;
import com.seater.smartmining.entity.ProjectDiggingMonthReport;
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
 * @Date 2019/1/28 0028 18:39
 */
public interface ProjectDiggingMonthReportRepository extends JpaRepository<ProjectDiggingMonthReport, Long>, JpaSpecificationExecutor<ProjectDiggingMonthReport> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_digging_month_report where project_id = ?1 and datediff(report_date, ?2) = 0")
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDay);

    List<ProjectDiggingMonthReport> getAllByProjectId(Long projectId);

    @Query(nativeQuery = true,value = "select * from project_digging_month_report where project_id = ?1 and datediff(report_date, ?2) = 0")
    List<ProjectDiggingMonthReport> getByProjectIdAndReportDate(Long projectId, Date reportDate);

    List<ProjectDiggingMonthReport> getByTotalId(Long totalId);

    @Transactional
    @Modifying
    @Query("update ProjectDiggingMonthReport set deduction = ?2, subsidyAmount = ?3,workTotalAmount = ?4  where id = ?1")
    void setDeductionAndSubsidyAmount(Long id, Long deduction, Long subsidyAmount, Long workTotalAmount);

    List<ProjectDiggingMonthReport> getByTotalIdAndOwnerId(Long totalId, Long ownerId);
}
