package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectDiggingDayReportDaoI;
import com.seater.smartmining.entity.ProjectDiggingDayReport;
import com.seater.smartmining.service.ProjectDiggingDayReportServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/2/12 0012 16:41
 */
@Service
public class ProjectDiggingDayReportServiceImpl implements ProjectDiggingDayReportServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectDiggingDayReportDaoI projectDiggingDayReportDaoI;

    @Override
    public ProjectDiggingDayReport get(Long id) throws IOException {
        return projectDiggingDayReportDaoI.get(id);
    }

    @Override
    public ProjectDiggingDayReport save(ProjectDiggingDayReport log) throws JsonProcessingException {
        return projectDiggingDayReportDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectDiggingDayReportDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectDiggingDayReportDaoI.delete(ids);
    }

    @Override
    public Page<ProjectDiggingDayReport> query() {
        return projectDiggingDayReportDaoI.query();
    }

    @Override
    public Page<ProjectDiggingDayReport> query(Specification<ProjectDiggingDayReport> spec) {
        return projectDiggingDayReportDaoI.query(spec);
    }

    @Override
    public Page<ProjectDiggingDayReport> query(Pageable pageable) {
        return projectDiggingDayReportDaoI.query(pageable);
    }

    @Override
    public Page<ProjectDiggingDayReport> query(Specification<ProjectDiggingDayReport> spec, Pageable pageable) {
        return projectDiggingDayReportDaoI.query(spec,pageable);
    }

    @Override
    public List<ProjectDiggingDayReport> getAll() {
        return projectDiggingDayReportDaoI.getAll();
    }

    @Override
    public List<ProjectDiggingDayReport> getByTotalId(Long totalId) {
        return projectDiggingDayReportDaoI.getByTotalId(totalId);
    }

    @Override
    public List<Map> getMonthReportByProjectIdAndReportDate(Long projectId, Date startDate, Date endDate) {
        return projectDiggingDayReportDaoI.getMonthReportByProjectIdAndReportDate(projectId, startDate, endDate);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate) {
        projectDiggingDayReportDaoI.deleteByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public Map getTotalInfoByProjectIdAndTime(Long projectId, Date reportDate) {
        return projectDiggingDayReportDaoI.getTotalInfoByProjectIdAndTime(projectId, reportDate);
    }

    @Override
    public Map getGrandInfoByProjectIdAndTime(Long projectId, Date reportDate) {
        return projectDiggingDayReportDaoI.getGrandInfoByProjectIdAndTime(projectId, reportDate);
    }

    @Override
    public Map getHistoryInfoByProjectId(Long projectId) {
        return projectDiggingDayReportDaoI.getHistoryInfoByProjectId(projectId);
    }

    @Override
    public void setDeductionTimeByDayAndDeductionTimeByNightOrderById(Long id, BigDecimal deductionTimeByDay, BigDecimal deductionTimeByNight) {
        projectDiggingDayReportDaoI.setDeductionTimeByDayAndDeductionTimeByNightOrderById(id, deductionTimeByDay, deductionTimeByNight);
    }

    @Override
    public List<Map> getCubicDetailByProjectIdAndReportDateAndMachineId(Long projectId, Date startTime, Date endTime, Long machineId) {
        return projectDiggingDayReportDaoI.getCubicDetailByProjectIdAndReportDateAndMachineId(projectId, startTime, endTime, machineId);
    }

    @Override
    public List<ProjectDiggingDayReport> queryWx(Specification<ProjectDiggingDayReport> spec) {
        return projectDiggingDayReportDaoI.queryWx(spec);
    }

    @Override
    public List<ProjectDiggingDayReport> getAllByProjectIdAndMachineIdAndReportDate(Long projectId, Long machineId, Date reportDate) {
        return projectDiggingDayReportDaoI.getAllByProjectIdAndMachineIdAndReportDate(projectId, machineId, reportDate);
    }

    @Override
    public void batchSave(List<ProjectDiggingDayReport> reportList) {
        projectDiggingDayReportDaoI.batchSave(reportList);
    }
}
