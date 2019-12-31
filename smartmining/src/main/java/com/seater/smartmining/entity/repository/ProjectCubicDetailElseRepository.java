package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectCubicDetailElse;
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
 * @Date 2019/3/5 0005 10:41
 */
public interface ProjectCubicDetailElseRepository extends JpaRepository<ProjectCubicDetailElse, Long>, JpaSpecificationExecutor<ProjectCubicDetailElse> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_cubic_detail_else where project_id = ?1 and datediff(create_date, ?2) = 0 and machine_id = ?3")
    void  deleteByProjectIdAndCreateDateAndMachineId(Long projectId, Date createDate, Long machineId);

    @Query(nativeQuery = true, value = "select * from project_cubic_detail_else where project_id = ?1 and total_id = ?2 and datediff(report_date, ?3) = 0")
    List<ProjectCubicDetailElse> getAllByProjectIdAndTotalId(Long projectId, Long totalId, Date reportDate);

}
