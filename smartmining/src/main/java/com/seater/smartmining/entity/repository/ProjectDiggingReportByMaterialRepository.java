package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectDiggingReportByMaterial;
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
 * @Date 2019/8/20 0020 11:15
 */
public interface ProjectDiggingReportByMaterialRepository extends JpaRepository<ProjectDiggingReportByMaterial, Long>, JpaSpecificationExecutor<ProjectDiggingReportByMaterial> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_digging_report_by_material where project_id = ?1 and datediff(date_identification, ?2) = 0")
    void deleteByProjectIdAndDateIdentification(Long projectId, Date date);
}
