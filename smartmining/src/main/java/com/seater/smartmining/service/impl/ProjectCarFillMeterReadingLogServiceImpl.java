package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectCarFillMeterReadingLogDaoI;
import com.seater.smartmining.entity.ProjectCarFillMeterReadingLog;
import com.seater.smartmining.service.ProjectCarFillMeterReadingLogServiceI;
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
public class ProjectCarFillMeterReadingLogServiceImpl implements ProjectCarFillMeterReadingLogServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectCarFillMeterReadingLogDaoI projectCarFillMeterReadingLogDaoI;

    @Override
    public ProjectCarFillMeterReadingLog get(Long id) throws IOException {
        return projectCarFillMeterReadingLogDaoI.get(id);
    }

    @Override
    public ProjectCarFillMeterReadingLog save(ProjectCarFillMeterReadingLog log) throws IOException{
        return projectCarFillMeterReadingLogDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectCarFillMeterReadingLogDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectCarFillMeterReadingLogDaoI.delete(ids);
    }

    @Override
    public Page<ProjectCarFillMeterReadingLog> query(Pageable pageable) {
        return projectCarFillMeterReadingLogDaoI.query(pageable);
    }

    @Override
    public Page<ProjectCarFillMeterReadingLog> query() {
        return projectCarFillMeterReadingLogDaoI.query();
    }

    @Override
    public Page<ProjectCarFillMeterReadingLog> query(Specification<ProjectCarFillMeterReadingLog> spec) {
        return projectCarFillMeterReadingLogDaoI.query(spec);
    }

    @Override
    public Page<ProjectCarFillMeterReadingLog> query(Specification<ProjectCarFillMeterReadingLog> spec, Pageable pageable) {
        return projectCarFillMeterReadingLogDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectCarFillMeterReadingLog> getAll() {
        return projectCarFillMeterReadingLogDaoI.getAll();
    }

    @Override
    public ProjectCarFillMeterReadingLog getByProjectIdAndOilCarIdAndAddTime(Long projectId,Long oilCarId, Date addTime) {
        return projectCarFillMeterReadingLogDaoI.getByProjectIdAndOilCarIdAndAddTime(projectId, oilCarId, addTime);
    }

    @Override
    public List<ProjectCarFillMeterReadingLog> queryWx(Specification<ProjectCarFillMeterReadingLog> spec) {
        return projectCarFillMeterReadingLogDaoI.queryWx(spec);
    }

    @Override
    public ProjectCarFillMeterReadingLog querySingle(Specification<ProjectCarFillMeterReadingLog> spec) {
        return projectCarFillMeterReadingLogDaoI.querySingle(spec);
    }

    @Override
    public Long getHistoryByOilCarId(Long oilCarId) {
        return projectCarFillMeterReadingLogDaoI.getHistoryByOilCarId(oilCarId);
    }

}
