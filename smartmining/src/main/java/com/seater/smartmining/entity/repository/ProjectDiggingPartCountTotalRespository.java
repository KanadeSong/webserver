package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectDiggingPartCountGrand;
import com.seater.smartmining.entity.ProjectDiggingPartCountTotal;
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
 * @Date 2019/2/28 0028 15:40
 */
public interface ProjectDiggingPartCountTotalRespository extends JpaRepository<ProjectDiggingPartCountTotal, Long>, JpaSpecificationExecutor<ProjectDiggingPartCountTotal> {

    @Query(nativeQuery = true, value = "select * from project_digging_part_count_total where project_id = ?1 and datediff(report_date,?2) = 0 and machine_id = ?3")
    List<ProjectDiggingPartCountTotal> getAllByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machineId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_digging_part_count_total where project_id = ?1 and datediff(report_date, ?2) = 0 and machine_id = ?3")
    void deleteByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machineId);
}
