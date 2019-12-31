package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectMqttCardReportDaoI;
import com.seater.smartmining.entity.ProjectMqttCardReport;
import com.seater.smartmining.service.ProjectMqttCardReportServiceI;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @Date 2019/11/3 0003 14:58
 */
@Service
public class ProjectMqttCardReportServiceImpl implements ProjectMqttCardReportServiceI {
    @Autowired
    private ProjectMqttCardReportDaoI projectMqttCardReportDaoI;

    @Override
    public ProjectMqttCardReport get(Long id) throws IOException {
        return projectMqttCardReportDaoI.get(id);
    }

    @Override
    public ProjectMqttCardReport save(ProjectMqttCardReport log) throws JsonProcessingException {
        return projectMqttCardReportDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectMqttCardReportDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectMqttCardReportDaoI.delete(ids);
    }

    @Override
    public Page<ProjectMqttCardReport> query() {
        return projectMqttCardReportDaoI.query();
    }

    @Override
    public Page<ProjectMqttCardReport> query(Specification<ProjectMqttCardReport> spec) {
        return projectMqttCardReportDaoI.query(spec);
    }

    @Override
    public Page<ProjectMqttCardReport> query(Pageable pageable) {
        return projectMqttCardReportDaoI.query(pageable);
    }

    @Override
    public Page<ProjectMqttCardReport> query(Specification<ProjectMqttCardReport> spec, Pageable pageable) {
        return projectMqttCardReportDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectMqttCardReport> getAll() {
        return projectMqttCardReportDaoI.getAll();
    }

    @Override
    public void deleteAllByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift) {
        projectMqttCardReportDaoI.deleteAllByProjectIdAndDateIdentificationAndShift(projectId, date, shift);
    }

    @Override
    public List<ProjectMqttCardReport> getAllByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift) {
        return projectMqttCardReportDaoI.getAllByProjectIdAndDateIdentificationAndShift(projectId, date, shift);
    }

    @Override
    public List<ProjectMqttCardReport> getAllByProjectIdAndCarCodeAndTimeDischarge(Long projectId, String carCode, Date startTime, Date endTime) {
        return projectMqttCardReportDaoI.getAllByProjectIdAndCarCodeAndTimeDischarge(projectId, carCode, startTime, endTime);
    }

    @Override
    public List<ProjectMqttCardReport> getAllByProjectIdAndTimeDischarge(Long projectId, Date startTime, Date endTime) {
        return projectMqttCardReportDaoI.getAllByProjectIdAndTimeDischarge(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getReportCountByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift) {
        return projectMqttCardReportDaoI.getReportCountByProjectIdAndDateIdentificationAndShift(projectId, date, shift);
    }

    @Override
    public List<Map> getErrorCountByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift) {
        return projectMqttCardReportDaoI.getErrorCountByProjectIdAndDateIdentificationAndShift(projectId, date, shift);
    }

    @Override
    public List<Map> getErrorCodeByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift) {
        return projectMqttCardReportDaoI.getErrorCodeByProjectIdAndDateIdentificationAndShift(projectId, date, shift);
    }

    @Override
    public Map getTotalCountByProjectIdAndCarCodeAndDateIdentificationAndShift(Long projectId, String carCode, Date date, Integer shift) {
        return projectMqttCardReportDaoI.getTotalCountByProjectIdAndCarCodeAndDateIdentificationAndShift(projectId, carCode, date, shift);
    }

    @Override
    public List<Map> getUnValidCountByProjectIdAndDateIdentification(Long projectId, Date startTime, Date endTime) {
        return projectMqttCardReportDaoI.getUnValidCountByProjectIdAndDateIdentification(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getUnValidCountMonthByProjectIdAndDateIdentification(Long projectId, Date startTime, Date endTime) {
        return projectMqttCardReportDaoI.getUnValidCountMonthByProjectIdAndDateIdentification(projectId, startTime, endTime);
    }

    @Override
    public List<ProjectMqttCardReport> getAllByProjectIdAndCarCodeAndDateIdentificationAndShift(Long projectId, String carCode, Date date, Integer shift) {
        return projectMqttCardReportDaoI.getAllByProjectIdAndCarCodeAndDateIdentificationAndShift(projectId, carCode, date, shift);
    }
}
