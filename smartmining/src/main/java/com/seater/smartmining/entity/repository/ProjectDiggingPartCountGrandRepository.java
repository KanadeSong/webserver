package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectDiggingPartCountGrand;
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
 * @Date 2019/2/28 0028 17:11
 */
public interface ProjectDiggingPartCountGrandRepository extends JpaRepository<ProjectDiggingPartCountGrand, Long>, JpaSpecificationExecutor<ProjectDiggingPartCountGrand> {

    @Query(nativeQuery = true, value = "select * from project_digging_part_count_grand where project_id = ?1 and datediff(report_date,?2) = 0 and machine_id = ?3")
    List<ProjectDiggingPartCountGrand> getAllByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machineId);


    ProjectDiggingPartCountGrand getAllByProjectIdAndTotalId(Long projectId, Long totalId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_digging_part_count_grand where project_id = ?1 and datediff(report_date, ?2) = 0 and machine_id = ?3")
    void deleteByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machineId);
}
