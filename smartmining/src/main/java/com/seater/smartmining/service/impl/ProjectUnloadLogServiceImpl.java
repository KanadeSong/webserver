package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectUnloadLogDaoI;
import com.seater.smartmining.entity.ProjectUnloadLog;
import com.seater.smartmining.service.ProjectUnloadLogServiceI;
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
public class ProjectUnloadLogServiceImpl implements ProjectUnloadLogServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectUnloadLogDaoI projectUnloadLogDaoI;

    @Override
    public ProjectUnloadLog get(Long id) throws IOException{
        return projectUnloadLogDaoI.get(id);
    }

    @Override
    public ProjectUnloadLog save(ProjectUnloadLog log) throws IOException{
        return projectUnloadLogDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectUnloadLogDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectUnloadLogDaoI.delete(ids);
    }

    @Override
    public Page<ProjectUnloadLog> query(Pageable pageable) {
        return projectUnloadLogDaoI.query(pageable);
    }

    @Override
    public Page<ProjectUnloadLog> query() {
        return projectUnloadLogDaoI.query();
    }

    @Override
    public Page<ProjectUnloadLog> query(Specification<ProjectUnloadLog> spec) {
        return projectUnloadLogDaoI.query(spec);
    }

    @Override
    public Page<ProjectUnloadLog> query(Specification<ProjectUnloadLog> spec, Pageable pageable) {
        return projectUnloadLogDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectUnloadLog> queryParams(Specification<ProjectUnloadLog> spec) {
        return projectUnloadLogDaoI.queryParams(spec);
    }

    @Override
    public List<ProjectUnloadLog> getAll() {
        return projectUnloadLogDaoI.getAll();
    }

    @Override
    public Date getMaxUnloadDateByCarCode(String carCode, Date date) {
        return projectUnloadLogDaoI.getMaxUnloadDateByCarCode(carCode, date);
    }

    @Override
    public List<ProjectUnloadLog> getAllByRecviceDate(Date receiveDate) {
        return projectUnloadLogDaoI.getAllByRecviceDate(receiveDate);
    }

    @Override
    public List<ProjectUnloadLog> getAllByProjectIDAndTimeDischarge(Long projectId, Date startDate, Date endDate, String uid) {
        return projectUnloadLogDaoI.getAllByProjectIDAndTimeDischarge(projectId, startDate, endDate, uid);
    }

    @Override
    public List<ProjectUnloadLog> queryAll(Specification<ProjectUnloadLog> specification) {
        return projectUnloadLogDaoI.queryAll(specification);
    }

    @Override
    public List<ProjectUnloadLog> getAllByProjectIDAndTime(Long projectId, Date startTime, Date endTime) {
        return projectUnloadLogDaoI.getAllByProjectIDAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getCarCodeByProjectIDAndTime(Long projectId, Date startTime, Date endTime) {
        return projectUnloadLogDaoI.getCarCodeByProjectIDAndTime(projectId, startTime,endTime);
    }

    @Override
    public List<Map> getCarCountByProjectIDAndTime(Long projectId, Date startTime, Date endTime, Date uploadTime) {
        return projectUnloadLogDaoI.getCarCountByProjectIDAndTime(projectId, startTime, endTime, uploadTime);
    }

    @Override
    public List<Map> getUnValidByProjectIDAndTime(Long projectId, Date startTime, Date endTime) {
        return projectUnloadLogDaoI.getUnValidByProjectIDAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getUploadCountByCheck(Long projectId, Date startTime, Date endTime, Date checkTime) {
        return projectUnloadLogDaoI.getUploadCountByCheck(projectId, startTime, endTime, checkTime);
    }

    @Override
    public List<Map> getReportInfoGroupBySlagSite(Long projectId, Date startTime, Date endTime, List<Long> ids) {
        return projectUnloadLogDaoI.getReportInfoGroupBySlagSite(projectId, startTime, endTime, ids);
    }

    @Override
    public List<Map> getReportInfoGroup(Long projectId, Date startTime, Date endTime) {
        return projectUnloadLogDaoI.getReportInfoGroup(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getTotalReportInfoByCarCode(Long projectId, Date startTime, Date endTime) {
        return projectUnloadLogDaoI.getTotalReportInfoByCarCode(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getTotalReportInfoByCarCodeAndSlagSite(Long projectId, Date startTime, Date endTime, List<Long> ids) {
        return projectUnloadLogDaoI.getTotalReportInfoByCarCodeAndSlagSite(projectId, startTime, endTime, ids);
    }

    @Override
    public ProjectUnloadLog getAllByProjectIDAndTimeDischargeAndCarCode(Long projectId, Date timeDischarge, String carCode) {
        return projectUnloadLogDaoI.getAllByProjectIDAndTimeDischargeAndCarCode(projectId, timeDischarge, carCode);
    }

    @Override
    public List<ProjectUnloadLog> getAllByProjectIDAndTimeDischargeAndIsVaild() {
        return projectUnloadLogDaoI.getAllByProjectIDAndTimeDischargeAndIsVaild();
    }

    @Override
    public Map getTotalCountByProjectIDAndTimeDischarge(Long projectId, Date startTime, Date endTime) {
        return projectUnloadLogDaoI.getTotalCountByProjectIDAndTimeDischarge(projectId, startTime, endTime);
    }

    @Override
    public List<ProjectUnloadLog> getAllByProjectIDAndTimeDischargeAndIsVaildAndDetail(Long projectId, Date startTime, Date endTime, Boolean valid, Boolean detail) {
        return projectUnloadLogDaoI.getAllByProjectIDAndTimeDischargeAndIsVaildAndDetail(projectId, startTime, endTime, valid, detail);
    }
}
