package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectScheduleLogDaoI;
import com.seater.smartmining.entity.ProjectScheduleLog;
import com.seater.smartmining.service.ProjectScheduleLogServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
 * @Date 2019/7/23 0023 10:13
 */
@Service
public class ProjectScheduleLogServiceImpl implements ProjectScheduleLogServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectScheduleLogDaoI projectScheduleLogDaoI;

    @Override
    public ProjectScheduleLog get(Long id) throws IOException {
        return projectScheduleLogDaoI.get(id);
    }

    @Override
    public ProjectScheduleLog save(ProjectScheduleLog log) throws JsonProcessingException {
        return projectScheduleLogDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectScheduleLogDaoI.delete(id);
    }

    @Override
    public Page<ProjectScheduleLog> query() {
        return projectScheduleLogDaoI.query();
    }

    @Override
    public Page<ProjectScheduleLog> query(Specification<ProjectScheduleLog> spec) {
        return projectScheduleLogDaoI.query(spec);
    }

    @Override
    public Page<ProjectScheduleLog> query(Pageable pageable) {
        return projectScheduleLogDaoI.query(pageable);
    }

    @Override
    public Page<ProjectScheduleLog> query(Specification<ProjectScheduleLog> spec, Pageable pageable) {
        return projectScheduleLogDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectScheduleLog> getAll() {
        return projectScheduleLogDaoI.getAll();
    }
}
