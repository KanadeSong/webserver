package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectCheckLogDaoI;
import com.seater.smartmining.entity.ProjectCheckLog;
import com.seater.smartmining.service.ProjectCheckLogServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ProjectCheckLogServiceImpl implements ProjectCheckLogServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectCheckLogDaoI projectCheckLogDaoI;

    @Override
    public ProjectCheckLog get(Long id) throws IOException{
        return projectCheckLogDaoI.get(id);
    }

    @Override
    public ProjectCheckLog save(ProjectCheckLog log) throws IOException{
        return projectCheckLogDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectCheckLogDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectCheckLogDaoI.delete(ids);
    }

    @Override
    public Page<ProjectCheckLog> query(Pageable pageable) {
        return projectCheckLogDaoI.query(pageable);
    }

    @Override
    public Page<ProjectCheckLog> query() {
        return projectCheckLogDaoI.query();
    }

    @Override
    public Page<ProjectCheckLog> query(Specification<ProjectCheckLog> spec) {
        return projectCheckLogDaoI.query(spec);
    }

    @Override
    public Page<ProjectCheckLog> query(Specification<ProjectCheckLog> spec, Pageable pageable) {
        return projectCheckLogDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectCheckLog> getAll() {
        return projectCheckLogDaoI.getAll();
    }

    @Override
    public List<Map> getCheckCountByProjectIDAndTimeCheck(Long projectId, Date startTime, Date endTime) {
        return projectCheckLogDaoI.getCheckCountByProjectIDAndTimeCheck(projectId, startTime, endTime);
    }
}
