package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectMqttUpdateExctDaoI;
import com.seater.smartmining.entity.ProjectMqttUpdateExct;
import com.seater.smartmining.service.ProjectMqttUpdateExctServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/6 0006 17:38
 */
@Service
public class ProjectMqttUpdateExctServiceImpl implements ProjectMqttUpdateExctServiceI {
    @Autowired
    private ProjectMqttUpdateExctDaoI projectMqttUpdateExctDaoI;

    @Override
    public ProjectMqttUpdateExct get(Long id) throws IOException {
        return projectMqttUpdateExctDaoI.get(id);
    }

    @Override
    public ProjectMqttUpdateExct save(ProjectMqttUpdateExct log) throws JsonProcessingException {
        return projectMqttUpdateExctDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectMqttUpdateExctDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectMqttUpdateExctDaoI.delete(ids);
    }

    @Override
    public Page<ProjectMqttUpdateExct> query() {
        return projectMqttUpdateExctDaoI.query();
    }

    @Override
    public Page<ProjectMqttUpdateExct> query(Specification<ProjectMqttUpdateExct> spec) {
        return projectMqttUpdateExctDaoI.query(spec);
    }

    @Override
    public Page<ProjectMqttUpdateExct> query(Pageable pageable) {
        return projectMqttUpdateExctDaoI.query(pageable);
    }

    @Override
    public Page<ProjectMqttUpdateExct> query(Specification<ProjectMqttUpdateExct> spec, Pageable pageable) {
        return projectMqttUpdateExctDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectMqttUpdateExct> getAll() {
        return projectMqttUpdateExctDaoI.getAll();
    }

    @Override
    public List<ProjectMqttUpdateExct> getAllByProjectIDAndSlagcarCodeAndCreateTime(Long projectId, String carCode, Date startTime, Date endTime) {
        return projectMqttUpdateExctDaoI.getAllByProjectIDAndSlagcarCodeAndCreateTime(projectId, carCode, startTime, endTime);
    }
}
