package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectCarTotalCountReport;
import com.seater.smartmining.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/19 0019 11:29
 */
public interface ProjectCarTotalCountReportRepository extends JpaRepository<ProjectCarTotalCountReport, Long>, JpaSpecificationExecutor<ProjectCarTotalCountReport> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_car_total_count_report where project_id = ?1 and datediff(date_identification, ?2) = 0 and shift = ?3")
    void deleteByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift);
}
