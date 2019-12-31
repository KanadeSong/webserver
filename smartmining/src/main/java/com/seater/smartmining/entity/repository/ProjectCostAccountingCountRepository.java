package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectCostAccountingCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/2/22 0022 11:21
 */
public interface ProjectCostAccountingCountRepository extends JpaRepository<ProjectCostAccountingCount, Long>, JpaSpecificationExecutor<ProjectCostAccountingCount> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_cost_accounting_count where project_id = ?1 and datediff(report_date, ?2) = 0")
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate);

    @Query(nativeQuery = true, value = "select * from project_cost_accounting_count where project_id = ?1 and datediff(report_date, ?2) = 0 order by statistics_type")
    List<ProjectCostAccountingCount> getByProjectIdAndReportDate(Long projectId, Date reportDate);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update ProjectCostAccountingCount set measure = ?2, deductionTimeByNight = ?3 where id = ?1")
    void setInfoById(Long id);
}
