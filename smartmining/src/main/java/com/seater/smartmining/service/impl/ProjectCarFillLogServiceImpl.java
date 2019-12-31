package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectCarFillLogDaoI;
import com.seater.smartmining.entity.ProjectCarFillLog;
import com.seater.smartmining.service.ProjectCarFillLogServiceI;
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
public class ProjectCarFillLogServiceImpl implements ProjectCarFillLogServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectCarFillLogDaoI projectCarFillLogDaoI;

    @Override
    public ProjectCarFillLog get(Long id) throws IOException {
        return projectCarFillLogDaoI.get(id);
    }

    @Override
    public ProjectCarFillLog save(ProjectCarFillLog log) throws IOException{
        return projectCarFillLogDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectCarFillLogDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectCarFillLogDaoI.delete(ids);
    }

    @Override
    public Page<ProjectCarFillLog> query(Pageable pageable) {
        return projectCarFillLogDaoI.query(pageable);
    }

    @Override
    public Page<ProjectCarFillLog> query() {
        return projectCarFillLogDaoI.query();
    }

    @Override
    public Page<ProjectCarFillLog> query(Specification<ProjectCarFillLog> spec) {
        return projectCarFillLogDaoI.query(spec);
    }

    @Override
    public Page<ProjectCarFillLog> query(Specification<ProjectCarFillLog> spec, Pageable pageable) {
        return projectCarFillLogDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectCarFillLog> getAll() {
        return projectCarFillLogDaoI.getAll();
    }

    @Override
    public List<ProjectCarFillLog> getByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarFillLogDaoI.getByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getCarGrandTotalFillByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return  projectCarFillLogDaoI.getCarGrandTotalFillByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getDiggingGrandTotalFillByProjectIdAndTime(String code, Long projectId, Date startTime, Date endTime) {
        return projectCarFillLogDaoI.getDiggingGrandTotalFillByProjectIdAndTime(code, projectId, startTime, endTime);
    }

    @Override
    public List<Map> getDiggingTotalFillByProjectIdAndTimeGroupByCar(Long projectId, Date startTime, Date endTime) {
        return projectCarFillLogDaoI.getDiggingTotalFillByProjectIdAndTimeGroupByCar(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getDiggingTotalFillByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarFillLogDaoI.getDiggingTotalFillByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getHistoryDiggingTotalFillByProjectId(Long projectId) {
        return projectCarFillLogDaoI.getHistoryDiggingTotalFillByProjectId(projectId);
    }

    @Override
    public Map getCarFillByProjectIdAAndCarIdAndTime(Long projectId, Long carId, Date beginDate, Date endDate) {
        return projectCarFillLogDaoI.getCarFillByProjectIdAAndCarIdAndTime(projectId, carId , beginDate, endDate);
    }

    @Override
    public Map getCarFillByProjectIdAndCarIdAndTime(Long projectId, Long carId, Date date) {
        return projectCarFillLogDaoI.getCarFillByProjectIdAndCarIdAndTime(projectId, carId, date);
    }

    @Override
    public List<ProjectCarFillLog> queryWx(Specification<ProjectCarFillLog> spec) {
        return projectCarFillLogDaoI.queryWx(spec);
    }

    @Override
    public Map getDiggingFillByProjectIdAndCarIdAndTime(Long projectId, Long carId, Date startTime, Date endTime) {
        return projectCarFillLogDaoI.getDiggingFillByProjectIdAndCarIdAndTime(projectId, carId, startTime, endTime);
    }

    @Override
    public List<Map> getDiggingMachineIdByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarFillLogDaoI.getDiggingMachineIdByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public Map getAllByProjectIdAndDate(Long projectId, Date reportDate) {
        return projectCarFillLogDaoI.getAllByProjectIdAndDate(projectId, reportDate);
    }

    @Override
    public List<Map> getAllByProjectIdAndDateAndCarType(Long projectId, Date reportDate) {
        return projectCarFillLogDaoI.getAllByProjectIdAndDateAndCarType(projectId, reportDate);
    }

    @Override
    public List<Map> getFillLogReport(Long projectId, Date startTime, Date endTime, Integer carType) {
        return projectCarFillLogDaoI.getFillLogReport(projectId, startTime, endTime, carType);
    }

    @Override
    public List<Map> getFillLogReportMonth(Long projectId, Date startTime, Date endTime, Integer carType) {
        return projectCarFillLogDaoI.getFillLogReportMonth(projectId, startTime, endTime, carType);
    }

    @Override
    public List<Map> getFillLogReportHistory(Long projectId, Date endTime, Integer carType) {
        return projectCarFillLogDaoI.getFillLogReportHistory(projectId, endTime, carType);
    }

    @Override
    public List<Map> getFillLogOnCar(Long projectId, Date date, Integer carType) {
        return projectCarFillLogDaoI.getFillLogOnCar(projectId, date, carType);
    }

    @Override
    public List<Map> getFillLogOnCarMonth(Long projectId, Date startTime, Date endTime, Integer carType) {
        return projectCarFillLogDaoI.getFillLogOnCarMonth(projectId, startTime, endTime, carType);
    }
}
