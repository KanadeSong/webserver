package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectMonthReportTotalDaoI;
import com.seater.smartmining.entity.ProjectMonthReportTotal;
import com.seater.smartmining.service.ProjectMonthReportTotalServiceI;
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
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/2/19 0019 10:59
 */
@Service
public class ProjectMonthReportTotalServiceImpl implements ProjectMonthReportTotalServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectMonthReportTotalDaoI projectMonthReportTotalDaoI;

    @Override
    public ProjectMonthReportTotal get(Long id) throws IOException {
        return projectMonthReportTotalDaoI.get(id);
    }

    @Override
    public ProjectMonthReportTotal save(ProjectMonthReportTotal log) throws JsonProcessingException {
        return projectMonthReportTotalDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectMonthReportTotalDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectMonthReportTotalDaoI.delete(ids);
    }

    @Override
    public Page<ProjectMonthReportTotal> query() {
        return projectMonthReportTotalDaoI.query();
    }

    @Override
    public Page<ProjectMonthReportTotal> query(Specification<ProjectMonthReportTotal> spec) {
        return projectMonthReportTotalDaoI.query(spec);
    }

    @Override
    public Page<ProjectMonthReportTotal> query(Pageable pageable) {
        return projectMonthReportTotalDaoI.query(pageable);
    }

    @Override
    public Page<ProjectMonthReportTotal> query(Specification<ProjectMonthReportTotal> spec, Pageable pageable) {
        return projectMonthReportTotalDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectMonthReportTotal> getAll() {
        return projectMonthReportTotalDaoI.getAll();
    }

    @Override
    public List<ProjectMonthReportTotal> getByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectMonthReportTotalDaoI.getByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate) {
        projectMonthReportTotalDaoI.deleteByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public void setDeductionAndSubsidyAmount(Long id, Long deduction, Long subsidyAmount) {
        projectMonthReportTotalDaoI.setDeductionAndSubsidyAmount(id, deduction, subsidyAmount);
    }
}
