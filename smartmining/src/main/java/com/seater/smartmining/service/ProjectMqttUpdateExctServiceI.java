package com.seater.smartmining.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectMqttUpdateExct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/6 0006 17:37
 */
public interface ProjectMqttUpdateExctServiceI {

    ProjectMqttUpdateExct get(Long id) throws IOException;
    ProjectMqttUpdateExct save(ProjectMqttUpdateExct log) throws JsonProcessingException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectMqttUpdateExct> query();
    Page<ProjectMqttUpdateExct> query(Specification<ProjectMqttUpdateExct> spec);
    Page<ProjectMqttUpdateExct> query(Pageable pageable);
    Page<ProjectMqttUpdateExct> query(Specification<ProjectMqttUpdateExct> spec, Pageable pageable);
    List<ProjectMqttUpdateExct> getAll();
    List<ProjectMqttUpdateExct> getAllByProjectIDAndSlagcarCodeAndCreateTime(Long projectId, String carCode, Date startTime, Date endTime);
}
