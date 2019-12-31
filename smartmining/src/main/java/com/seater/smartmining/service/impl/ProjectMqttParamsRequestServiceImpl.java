package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectMqttParamsRequestDaoI;
import com.seater.smartmining.entity.ProjectMqttParamsRequest;
import com.seater.smartmining.service.ProjectMqttParamsRequestServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/2 0002 22:40
 */
@Service
public class ProjectMqttParamsRequestServiceImpl implements ProjectMqttParamsRequestServiceI {

    @Autowired
    private ProjectMqttParamsRequestDaoI projectMqttParamsRequestDaoI;

    @Override
    public ProjectMqttParamsRequest get(Long id) throws IOException {
        return projectMqttParamsRequestDaoI.get(id);
    }

    @Override
    public ProjectMqttParamsRequest save(ProjectMqttParamsRequest log) throws JsonProcessingException {
        return projectMqttParamsRequestDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectMqttParamsRequestDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectMqttParamsRequestDaoI.delete(ids);
    }

    @Override
    public Page<ProjectMqttParamsRequest> query() {
        return projectMqttParamsRequestDaoI.query();
    }

    @Override
    public Page<ProjectMqttParamsRequest> query(Specification<ProjectMqttParamsRequest> spec) {
        return projectMqttParamsRequestDaoI.query(spec);
    }

    @Override
    public Page<ProjectMqttParamsRequest> query(Pageable pageable) {
        return projectMqttParamsRequestDaoI.query(pageable);
    }

    @Override
    public Page<ProjectMqttParamsRequest> query(Specification<ProjectMqttParamsRequest> spec, Pageable pageable) {
        return projectMqttParamsRequestDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectMqttParamsRequest> getAll() {
        return projectMqttParamsRequestDaoI.getAll();
    }
}
