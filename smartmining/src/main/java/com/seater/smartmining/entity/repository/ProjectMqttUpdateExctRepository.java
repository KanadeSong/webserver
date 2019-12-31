package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectMqttUpdateExct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/6 0006 17:29
 */
public interface ProjectMqttUpdateExctRepository extends JpaRepository<ProjectMqttUpdateExct, Long>, JpaSpecificationExecutor<ProjectMqttUpdateExct> {

    @Query(nativeQuery = true, value = "select * from project_mqtt_update_exct where projectid = ?1 and slagcar_code = ?2 and create_time >= ?3 and create_time <= ?4" +
            " order by create_time desc")
    List<ProjectMqttUpdateExct> getAllByProjectIDAndSlagcarCodeAndCreateTime(Long projectId, String carCode, Date startTime, Date endTime);
}
