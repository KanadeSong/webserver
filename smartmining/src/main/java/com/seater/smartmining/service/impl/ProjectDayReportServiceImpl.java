package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectDayReportDaoI;
import com.seater.smartmining.entity.ProjectDayReport;
import com.seater.smartmining.service.ProjectDayReportServiceI;
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
public class ProjectDayReportServiceImpl implements ProjectDayReportServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectDayReportDaoI projectDayReportDaoI;

    @Override
    public ProjectDayReport get(Long id) throws IOException {
        return projectDayReportDaoI.get(id);
    }

    @Override
    public ProjectDayReport save(ProjectDayReport log) throws IOException{
        return projectDayReportDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectDayReportDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectDayReportDaoI.delete(ids);
    }

    @Override
    public Page<ProjectDayReport> query(Pageable pageable) {
        return projectDayReportDaoI.query(pageable);
    }

    @Override
    public Page<ProjectDayReport> query() {
        return projectDayReportDaoI.query();
    }

    @Override
    public Page<ProjectDayReport> query(Specification<ProjectDayReport> spec) {
        return projectDayReportDaoI.query(spec);
    }

    @Override
    public Page<ProjectDayReport> query(Specification<ProjectDayReport> spec, Pageable pageable) {
        return projectDayReportDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectDayReport> getAll() {
        return projectDayReportDaoI.getAll();
    }

    @Override
    public ProjectDayReport getByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectDayReportDaoI.getByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate) {
        projectDayReportDaoI.deleteByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public Map getOnDutyCountByProjectIdAndReportDate(Long projectId, Date startTime, Date endTime) {
        return projectDayReportDaoI.getOnDutyCountByProjectIdAndReportDate(projectId, startTime, endTime);
    }

    @Override
    public List<ProjectDayReport> getSettlementDetailByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectDayReportDaoI.getSettlementDetailByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<ProjectDayReport> getByProjectIdAndCreateDate(Long projectId, Date createDate) {
        return projectDayReportDaoI.getByProjectIdAndCreateDate(projectId, createDate);
    }

    @Override
    public List<Map> getAvgCarInfo(Long projectId, Date startTime, Date endTime) {
        return projectDayReportDaoI.getAvgCarInfo(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getAvgCarInfoMonth(Long projectId, Date startTime, Date endTime) {
        return projectDayReportDaoI.getAvgCarInfoMonth(projectId, startTime, endTime);
    }
}
