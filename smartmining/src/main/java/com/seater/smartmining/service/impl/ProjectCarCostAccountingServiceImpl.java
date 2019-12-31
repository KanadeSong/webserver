package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectCarCostAccountingDaoI;
import com.seater.smartmining.entity.ProjectCarCostAccounting;
import com.seater.smartmining.service.ProjectCarCostAccountingServiceI;
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
 * @Date 2019/2/22 0022 9:46
 */
@Service
public class ProjectCarCostAccountingServiceImpl implements ProjectCarCostAccountingServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectCarCostAccountingDaoI projectCarCostAccountingDaoI;

    @Override
    public ProjectCarCostAccounting get(Long id) throws IOException {
        return projectCarCostAccountingDaoI.get(id);
    }

    @Override
    public ProjectCarCostAccounting save(ProjectCarCostAccounting log) throws JsonProcessingException {
        return projectCarCostAccountingDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectCarCostAccountingDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectCarCostAccountingDaoI.delete(ids);
    }

    @Override
    public Page<ProjectCarCostAccounting> query() {
        return projectCarCostAccountingDaoI.query();
    }

    @Override
    public Page<ProjectCarCostAccounting> query(Specification<ProjectCarCostAccounting> spec) {
        return projectCarCostAccountingDaoI.query(spec);
    }

    @Override
    public Page<ProjectCarCostAccounting> query(Pageable pageable, Specification<ProjectCarCostAccounting> spec) {
        return projectCarCostAccountingDaoI.query(pageable, spec);
    }

    @Override
    public Page<ProjectCarCostAccounting> query(Pageable pageable) {
        return projectCarCostAccountingDaoI.query(pageable);
    }

    @Override
    public List<ProjectCarCostAccounting> getAll() {
        return projectCarCostAccountingDaoI.getAll();
    }

    @Override
    public List<ProjectCarCostAccounting> getAllByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectCarCostAccountingDaoI.getAllByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate) {
        projectCarCostAccountingDaoI.deleteByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public List<Map> getCarAmountReport(Long projectId, Date startTime, Date endTime) {
        return projectCarCostAccountingDaoI.getCarAmountReport(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getCarAmountReportMonth(Long projectId, Date startTime, Date endTime) {
        return projectCarCostAccountingDaoI.getCarAmountReportMonth(projectId, startTime, endTime);
    }

    @Override
    public Date getMaxReportDate() {
        List<Date> dateList = projectCarCostAccountingDaoI.getMaxReportDate();
        if(dateList.size() > 0)
            return dateList.get(0);
        return null;
    }

    @Override
    public List<ProjectCarCostAccounting> getAllByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarCostAccountingDaoI.getAllByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getHistoryAmount(Long projectId, Date startTime, Date endTime) {
        return projectCarCostAccountingDaoI.getHistoryAmount(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getHistoryAmountHistory(Long projectId, Date endTime) {
        return projectCarCostAccountingDaoI.getHistoryAmountHistory(projectId, endTime);
    }

    @Override
    public List<Map> getHistoryFillAmountAndAmount(Long projectId, Date startTime, Date endTime) {
        return projectCarCostAccountingDaoI.getHistoryFillAmountAndAmount(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getHistoryFillAmountAndAmountHistory(Long projectId, Date date) {
        return projectCarCostAccountingDaoI.getHistoryFillAmountAndAmountHistory(projectId, date);
    }

    @Override
    public List<Map> getHistoryFillAmountAndAmountMonth(Long projectId, Date startTime, Date endTime) {
        return projectCarCostAccountingDaoI.getHistoryFillAmountAndAmountMonth(projectId, startTime, endTime);
    }

    @Override
    public Map getAllByProjectIdAndReportDate(Long projectId, Date startTime, Date endTime) {
        return projectCarCostAccountingDaoI.getAllByProjectIdAndReportDate(projectId, startTime, endTime);
    }
}
