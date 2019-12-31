package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectSystemMqttLogDaoI;
import com.seater.smartmining.entity.ProjectSystemMqttLog;
import com.seater.smartmining.service.ProjectSystemMqttLogServiceI;
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
 * @Date 2019/11/21 0021 12:54
 */
@Service
public class ProjectSystemMqttLogServiceImpl implements ProjectSystemMqttLogServiceI {

    @Autowired
    private ProjectSystemMqttLogDaoI projectSystemMqttLogDaoI;
    @Override
    public ProjectSystemMqttLog get(Long id) throws IOException {
        return projectSystemMqttLogDaoI.get(id);
    }

    @Override
    public ProjectSystemMqttLog save(ProjectSystemMqttLog log) throws IOException {
        return projectSystemMqttLogDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectSystemMqttLogDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectSystemMqttLogDaoI.delete(ids);
    }

    @Override
    public Page<ProjectSystemMqttLog> query() {
        return projectSystemMqttLogDaoI.query();
    }

    @Override
    public Page<ProjectSystemMqttLog> query(Specification<ProjectSystemMqttLog> spec) {
        return projectSystemMqttLogDaoI.query(spec);
    }

    @Override
    public Page<ProjectSystemMqttLog> query(Pageable pageable) {
        return projectSystemMqttLogDaoI.query(pageable);
    }

    @Override
    public Page<ProjectSystemMqttLog> query(Specification<ProjectSystemMqttLog> spec, Pageable pageable) {
        return projectSystemMqttLogDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectSystemMqttLog> getAll() {
        return projectSystemMqttLogDaoI.getAll();
    }
}
