package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectMqttParamsRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/2 0002 22:34
 */
public interface ProjectMqttParamsRequestRepository extends JpaRepository<ProjectMqttParamsRequest,Long>, JpaSpecificationExecutor<ProjectMqttParamsRequest> {
}
