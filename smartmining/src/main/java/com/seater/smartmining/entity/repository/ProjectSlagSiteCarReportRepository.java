package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectSlagSiteCarReport;
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
 * @Date 2019/7/29 0029 15:10
 */
public interface ProjectSlagSiteCarReportRepository extends JpaRepository<ProjectSlagSiteCarReport, Long>, JpaSpecificationExecutor<ProjectSlagSiteCarReport> {

    @Query(nativeQuery = true, value = "select * from project_slag_site_car_report where project_id = ?1 and datediff(report_date, ?2) = 0")
    List<ProjectSlagSiteCarReport> getAllByProjectIdAndReportDate(Long projectId, Date reportDate);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_slag_site_car_report where project_id = ?1 and datediff(report_date, ?2) = 0")
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate);
}
