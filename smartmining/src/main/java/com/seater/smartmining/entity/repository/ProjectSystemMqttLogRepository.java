package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectSystemMqttLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/21 0021 12:48
 */
public interface ProjectSystemMqttLogRepository extends JpaRepository<ProjectSystemMqttLog, Long>, JpaSpecificationExecutor<ProjectSystemMqttLog> {


}
