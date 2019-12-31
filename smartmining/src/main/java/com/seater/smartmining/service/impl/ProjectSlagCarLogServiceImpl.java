package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectSlagCarLogDaoI;
import com.seater.smartmining.entity.ProjectSlagCarLog;
import com.seater.smartmining.service.ProjectSlagCarLogServiceI;
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

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/8/16 0016 17:01
 */
@Service
public class ProjectSlagCarLogServiceImpl implements ProjectSlagCarLogServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectSlagCarLogDaoI projectSlagCarLogDaoI;

    @Override
    public ProjectSlagCarLog get(Long id) throws IOException {
        return projectSlagCarLogDaoI.get(id);
    }

    @Override
    public ProjectSlagCarLog save(ProjectSlagCarLog log) throws IOException {
        return projectSlagCarLogDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectSlagCarLogDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectSlagCarLogDaoI.delete(ids);
    }

    @Override
    public Page<ProjectSlagCarLog> query() {
        return projectSlagCarLogDaoI.query();
    }

    @Override
    public Page<ProjectSlagCarLog> query(Specification<ProjectSlagCarLog> spec) {
        return projectSlagCarLogDaoI.query(spec);
    }

    @Override
    public Page<ProjectSlagCarLog> query(Pageable pageable) {
        return projectSlagCarLogDaoI.query(pageable);
    }

    @Override
    public Page<ProjectSlagCarLog> query(Specification<ProjectSlagCarLog> spec, Pageable pageable) {
        return projectSlagCarLogDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectSlagCarLog> getAll() {
        return projectSlagCarLogDaoI.getAll();
    }

    @Override
    public List<Map> getCarCountByProjectIDAndTime(Long projectId, Date startTime, Date endTime) {
        return projectSlagCarLogDaoI.getCarCountByProjectIDAndTime(projectId, startTime, endTime);
    }

    @Override
    public ProjectSlagCarLog getAllByProjectIDAndCarCodeAndTerminalTime(Long projectId, String carCode, Long terminalTime){
        return projectSlagCarLogDaoI.getAllByProjectIDAndCarCodeAndTerminalTime(projectId, carCode, terminalTime);
    }

    @Override
    public List<ProjectSlagCarLog> getAllByProjectIDAndTimeDischarge(Long projectId, Date startTime, Date endTime) {
        return projectSlagCarLogDaoI.getAllByProjectIDAndTimeDischarge(projectId, startTime, endTime);
    }
}
