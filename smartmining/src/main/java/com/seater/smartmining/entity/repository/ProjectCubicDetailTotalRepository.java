package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectCubicDetailTotal;
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
 * @Date 2019/3/4 0004 16:55
 */
public interface ProjectCubicDetailTotalRepository extends JpaRepository<ProjectCubicDetailTotal, Long>, JpaSpecificationExecutor<ProjectCubicDetailTotal> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_cubic_detail_total where project_id = ?1 and datediff(report_date, ?2) = 0 and machine_id = ?3")
    void  deleteByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machineId);

    @Query(nativeQuery = true, value = "select * from project_cubic_detail_total where project_id = ?1 and datediff(report_date, ?2) = 0 and machine_id = ?3")
    List<ProjectCubicDetailTotal> getAllByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machineId);
}
