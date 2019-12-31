package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectErrorLoadLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/1 0001 0:20
 */
public interface ProjectErrorLoadLogRepository extends JpaRepository<ProjectErrorLoadLog, Long>, JpaSpecificationExecutor<ProjectErrorLoadLog> {

    @Query(nativeQuery = true, value = "select * from project_error_load_log where project_id = ?1 and car_code = ?2 and date_identification = ?3 and shift = ?4")
    ProjectErrorLoadLog getAllByProjectIdAndCarCodeAndDateIdentificationAndShift(Long projectId, String carCode, Date dateIdentification, Integer shift);

    @Query(nativeQuery = true, value = "select * from project_error_load_log where project_id = ?1 and date_identification = ?2 and shift = ?3")
    List<ProjectErrorLoadLog> getAllByProjectIdAndDateIdentificationAndShift(Long projectId, Date dateIdentification, Integer shift);
}
