package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectCubicDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/4 0004 16:35
 */
public interface ProjectCubicDetailRepository extends JpaRepository<ProjectCubicDetail, Long>, JpaSpecificationExecutor<ProjectCubicDetail> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_cubic_detail where project_id = ?1 and datediff(create_date, ?2) = 0 and machine_id = ?3")
    void  deleteByProjectIdAndCreateDateAndMachineId(Long projectId, Date createDate, Long machineId);

    @Query(nativeQuery = true, value = "select * from project_cubic_detail where project_id = ?1 and total_id = ?2 and datediff(report_date, ?3) = 0")
    List<ProjectCubicDetail> getAllByProjectIdAndTotalId(Long projectId, Long totalId, Date reportDate);

    @Query(nativeQuery = true, value = "select report_date from project_cubic_detail " +
            " where project_id = ?1 and machine_id = ?2 and total_id = ?3 group by report_date")
    List<Map> getReportDateByProjectIdAndCarIdAndTotalId(Long projectId, Long machineId, Long totalId);

    @Query(nativeQuery = true, value = "select sum(cubics) as cubic, sum(cars) as count, material_id, material_name, machine_id from project_cubic_detail where project_id = ?1" +
            " and report_date >= ?2 and report_date <= ?3 group by material_id, material_name, machine_id order by machine_id")
    List<Map> getByProjectIdAndReportDate(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select sum(cubics) as cubic, sum(cars) as count, material_id, material_name from project_cubic_detail " +
            " where project_id = ?1 and report_date >= ?2 and report_date <= ?3 and machine_id = ?4 group by material_id, material_name")
    List<Map> getByProjectIdAndReportDateAndMachineId(Long projectId, Date startTime, Date endTime, Long machineId);

    @Query(nativeQuery = true, value = "select sum(cubics) as cubic, sum(cars) as count, material_id, material_name from project_cubic_detail " +
            " where project_id = ?1 and report_date >= ?2 and report_date <= ?3 group by material_id, material_name")
    List<Map> getByProjectIdAndDate(Long projectId, Date startTime, Date endTime);
}
