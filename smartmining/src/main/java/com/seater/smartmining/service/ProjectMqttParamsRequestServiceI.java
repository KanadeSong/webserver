package com.seater.smartmining.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectMqttParamsRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/2 0002 22:39
 */
public interface ProjectMqttParamsRequestServiceI {

    ProjectMqttParamsRequest get(Long id) throws IOException;
    ProjectMqttParamsRequest save(ProjectMqttParamsRequest log) throws JsonProcessingException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectMqttParamsRequest> query();
    Page<ProjectMqttParamsRequest> query(Specification<ProjectMqttParamsRequest> spec);
    Page<ProjectMqttParamsRequest> query(Pageable pageable);
    Page<ProjectMqttParamsRequest> query(Specification<ProjectMqttParamsRequest> spec, Pageable pageable);
    List<ProjectMqttParamsRequest> getAll();
}
