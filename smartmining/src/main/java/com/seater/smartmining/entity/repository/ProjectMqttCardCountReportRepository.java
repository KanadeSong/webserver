package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectMqttCardCountReport;
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
 * @Date 2019/11/13 0013 12:43
 */
public interface ProjectMqttCardCountReportRepository extends JpaRepository<ProjectMqttCardCountReport, Long>, JpaSpecificationExecutor<ProjectMqttCardCountReport> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_mqtt_card_count_report where project_id = ?1 and datediff(date_identification, ?2) = 0 and shift = ?3")
    void deleteByProjectIdAndCreateTime(Long projectId, Date date, Integer shift);
}
