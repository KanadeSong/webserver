package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectLoadLogDaoI;
import com.seater.smartmining.entity.ProjectLoadLog;
import com.seater.smartmining.service.ProjectLoadLogServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ProjectLoadLogServiceImpl implements ProjectLoadLogServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectLoadLogDaoI projectLoadLogDaoI;

    @Override
    public ProjectLoadLog get(Long id) throws IOException {
        return projectLoadLogDaoI.get(id);
    }

    @Override
    public ProjectLoadLog save(ProjectLoadLog log) throws IOException{
        return projectLoadLogDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectLoadLogDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectLoadLogDaoI.delete(ids);
    }

    @Override
    public Page<ProjectLoadLog> query(Pageable pageable) {
        return projectLoadLogDaoI.query(pageable);
    }

    @Override
    public Page<ProjectLoadLog> query() {
        return projectLoadLogDaoI.query();
    }

    @Override
    public Page<ProjectLoadLog> query(Specification<ProjectLoadLog> spec) {
        return projectLoadLogDaoI.query(spec);
    }

    @Override
    public Page<ProjectLoadLog> query(Specification<ProjectLoadLog> spec, Pageable pageable) {
        return projectLoadLogDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectLoadLog> getAll() {
        return projectLoadLogDaoI.getAll();
    }

    @Override
    public Date getMaxUnloadDateByCarCode(String carCode) {
        return projectLoadLogDaoI.getMaxUnloadDateByCarCode(carCode);
    }

    @Override
    public List<Map> getMachineCountByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectLoadLogDaoI.getMachineCountByProjectIdAndTime(projectId, startTime, endTime);
    }
}
