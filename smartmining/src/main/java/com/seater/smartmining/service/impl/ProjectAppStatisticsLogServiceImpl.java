package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectAppStatisticsLogDaoI;
import com.seater.smartmining.entity.ProjectAppStatisticsLog;
import com.seater.smartmining.service.ProjectAppStatisticsLogServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
 * @Date 2019/4/11 0011 16:48
 */
@Service
public class ProjectAppStatisticsLogServiceImpl implements ProjectAppStatisticsLogServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectAppStatisticsLogDaoI projectAppStatisticsLogDaoI;

    @Override
    public ProjectAppStatisticsLog get(Long id) throws IOException {
        return projectAppStatisticsLogDaoI.get(id);
    }

    @Override
    public ProjectAppStatisticsLog save(ProjectAppStatisticsLog log) throws JsonProcessingException {
        return projectAppStatisticsLogDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectAppStatisticsLogDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectAppStatisticsLogDaoI.delete(ids);
    }

    @Override
    public Page<ProjectAppStatisticsLog> query() {
        return projectAppStatisticsLogDaoI.query();
    }

    @Override
    public Page<ProjectAppStatisticsLog> query(Specification<ProjectAppStatisticsLog> spec) {
        return projectAppStatisticsLogDaoI.query(spec);
    }

    @Override
    public Page<ProjectAppStatisticsLog> query(Pageable pageable) {
        return projectAppStatisticsLogDaoI.query(pageable);
    }

    @Override
    public Page<ProjectAppStatisticsLog> query(Specification<ProjectAppStatisticsLog> spec, Pageable pageable) {
        return projectAppStatisticsLogDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectAppStatisticsLog> getAll() {
        return projectAppStatisticsLogDaoI.getAll();
    }

    @Override
    public void deleteByProjectAndReportDate(Long projectId, Date reportDate) {
        projectAppStatisticsLogDaoI.deleteByProjectAndReportDate(projectId, reportDate);
    }

    @Override
    public ProjectAppStatisticsLog getAllByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectAppStatisticsLogDaoI.getAllByProjectIdAndReportDate(projectId, reportDate).get(0);
    }
}
