package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectCarTotalCountReportByTotal;
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
 * @Date 2019/11/22 0022 17:11
 */
public interface ProjectCarTotalCountReportByTotalRepository extends JpaRepository<ProjectCarTotalCountReportByTotal, Long>, JpaSpecificationExecutor<ProjectCarTotalCountReportByTotal> {

    @Query(nativeQuery = true, value = "select * from project_car_total_count_report_by_total" +
            " where project_id = ?1 and datediff(date_identification, ?2) = 0 and shift = ?3")
    List<ProjectCarTotalCountReportByTotal> getAllByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_car_total_count_report_by_total" +
            " where project_id = ?1 and datediff(date_identification, ?2) = 0 and shift = ?3")
    void deleteByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift);
}
