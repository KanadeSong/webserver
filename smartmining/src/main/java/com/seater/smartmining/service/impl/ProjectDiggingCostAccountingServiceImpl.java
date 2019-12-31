package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectDiggingCostAccountingDaoI;
import com.seater.smartmining.entity.ProjectDiggingCostAccounting;
import com.seater.smartmining.service.ProjectDiggingCostAccountingServiceI;
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
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/2/21 0021 9:13
 */
@Service
public class ProjectDiggingCostAccountingServiceImpl implements ProjectDiggingCostAccountingServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectDiggingCostAccountingDaoI projectDiggingCostAccountingDaoI;

    @Override
    public ProjectDiggingCostAccounting get(Long id) throws IOException {
        return projectDiggingCostAccountingDaoI.get(id);
    }

    @Override
    public ProjectDiggingCostAccounting save(ProjectDiggingCostAccounting log) throws JsonProcessingException {
        return projectDiggingCostAccountingDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectDiggingCostAccountingDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectDiggingCostAccountingDaoI.delete(ids);
    }

    @Override
    public Page<ProjectDiggingCostAccounting> query() {
        return projectDiggingCostAccountingDaoI.query();
    }

    @Override
    public Page<ProjectDiggingCostAccounting> query(Specification<ProjectDiggingCostAccounting> spec) {
        return projectDiggingCostAccountingDaoI.query(spec);
    }

    @Override
    public Page<ProjectDiggingCostAccounting> query(Pageable pageable) {
        return projectDiggingCostAccountingDaoI.query(pageable);
    }

    @Override
    public Page<ProjectDiggingCostAccounting> query(Specification<ProjectDiggingCostAccounting> spec, Pageable pageable) {
        return projectDiggingCostAccountingDaoI.query(spec,pageable);
    }

    @Override
    public List<ProjectDiggingCostAccounting> getAll() {
        return projectDiggingCostAccountingDaoI.getAll();
    }

    @Override
    public List<ProjectDiggingCostAccounting> getAllByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectDiggingCostAccountingDaoI.getAllByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate) {
        projectDiggingCostAccountingDaoI.deleteByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public List<Map> getAllByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectDiggingCostAccountingDaoI.getAllByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getAllByProjectIdAndTimeMonth(Long projectId, Date startTime, Date endTime) {
        return projectDiggingCostAccountingDaoI.getAllByProjectIdAndTimeMonth(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getAllByProjectIdAndTimeHistory(Long projectId, Date endTime) {
        return projectDiggingCostAccountingDaoI.getAllByProjectIdAndTimeHistory(projectId, endTime);
    }

    @Override
    public Date getMaxReportDate() {
        List<Date> dateList = projectDiggingCostAccountingDaoI.getMaxReportDate();
        if(dateList.size() > 0)
            return dateList.get(0);
        return null;
    }

    @Override
    public List<Map> getHistoryAmount(Long projectId, Date startTime, Date endTime) {
        return projectDiggingCostAccountingDaoI.getHistoryAmount(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getHistoryAmountHistory(Long projectId, Date endDate) {
        return projectDiggingCostAccountingDaoI.getHistoryAmountHistory(projectId, endDate);
    }

    @Override
    public List<Map> getHistoryFillAmountAndAmount(Long projectId, Date startTime, Date endTime) {
        return projectDiggingCostAccountingDaoI.getHistoryFillAmountAndAmount(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getHistoryFillAmountAndAmountMonth(Long projectId, Date startTime, Date endTime) {
        return projectDiggingCostAccountingDaoI.getHistoryFillAmountAndAmountMonth(projectId, startTime, endTime);
    }

    @Override
    public Map getAllByProjectIdAndReportDate(Long projectId, Date startTime, Date endTime) {
        return projectDiggingCostAccountingDaoI.getAllByProjectIdAndReportDate(projectId, startTime, endTime);
    }
}
