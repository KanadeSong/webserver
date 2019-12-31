package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectDayReportPartDistanceDaoI;
import com.seater.smartmining.entity.ProjectDayReportPartDistance;
import com.seater.smartmining.service.ProjectDayReportPartDistanceServiceI;
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
public class ProjectDayReportPartDistanceServiceImpl implements ProjectDayReportPartDistanceServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectDayReportPartDistanceDaoI projectDayReportPartDistanceDaoI;

    @Override
    public ProjectDayReportPartDistance get(Long id) throws IOException {
        return projectDayReportPartDistanceDaoI.get(id);
    }

    @Override
    public ProjectDayReportPartDistance save(ProjectDayReportPartDistance log) throws IOException{
        return projectDayReportPartDistanceDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectDayReportPartDistanceDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectDayReportPartDistanceDaoI.delete(ids);
    }

    @Override
    public Page<ProjectDayReportPartDistance> query(Pageable pageable) {
        return projectDayReportPartDistanceDaoI.query(pageable);
    }

    @Override
    public Page<ProjectDayReportPartDistance> query() {
        return projectDayReportPartDistanceDaoI.query();
    }

    @Override
    public Page<ProjectDayReportPartDistance> query(Specification<ProjectDayReportPartDistance> spec) {
        return projectDayReportPartDistanceDaoI.query(spec);
    }

    @Override
    public Page<ProjectDayReportPartDistance> query(Specification<ProjectDayReportPartDistance> spec, Pageable pageable) {
        return projectDayReportPartDistanceDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectDayReportPartDistance> getAll() {
        return projectDayReportPartDistanceDaoI.getAll();
    }

    @Override
    public List<ProjectDayReportPartDistance> getByReportIdOrderByDistance(Long reportId) {
        return projectDayReportPartDistanceDaoI.getByReportIdOrderByDistance(reportId);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate) {
        projectDayReportPartDistanceDaoI.deleteByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public List<Map> getByProjectIdAndReportId(Long projectId, Long reportId) {
        return projectDayReportPartDistanceDaoI.getByProjectIdAndReportId(projectId,reportId);
    }
}
