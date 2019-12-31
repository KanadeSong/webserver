package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectDayReportPartDistance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ProjectDayReportPartDistanceRepository extends JpaRepository<ProjectDayReportPartDistance, Long>, JpaSpecificationExecutor<ProjectDayReportPartDistance> {
    List<ProjectDayReportPartDistance> getByReportIdOrderByDistance(Long reportId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_day_report_part_distance where project_id = ?1 and datediff(report_date, ?2) = 0")
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate);

    @Query(nativeQuery = true, value = "select distance,sum(total_amount) as total_amount,sum(total_cubic) as total_cubic,sum(total_count) as total_count from project_day_report_part_distance " +
            " where project_id = ?1 and report_id = ?2 group by distance")
    List<Map> getByProjectIdAndReportId(Long projectId, Long reportId);
}
