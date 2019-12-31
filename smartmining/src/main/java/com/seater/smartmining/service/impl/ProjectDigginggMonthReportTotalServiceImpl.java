package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectDiggingMonthReportTotalDaoI;
import com.seater.smartmining.entity.ProjectDiggingMonthReport;
import com.seater.smartmining.entity.ProjectDiggingMonthReportTotal;
import com.seater.smartmining.service.ProjectDigginggMonthReportTotalServiceI;
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
 * @Date 2019/2/16 0016 13:44
 */
@Service
public class ProjectDigginggMonthReportTotalServiceImpl implements ProjectDigginggMonthReportTotalServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectDiggingMonthReportTotalDaoI projectDiggingMonthReportTotalDaoI;

    @Override
    public ProjectDiggingMonthReportTotal get(Long id) throws IOException {
        return projectDiggingMonthReportTotalDaoI.get(id);
    }

    @Override
    public ProjectDiggingMonthReportTotal save(ProjectDiggingMonthReportTotal log) throws IOException {
        return projectDiggingMonthReportTotalDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectDiggingMonthReportTotalDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectDiggingMonthReportTotalDaoI.delete(ids);
    }

    @Override
    public Page<ProjectDiggingMonthReportTotal> query() {
        return projectDiggingMonthReportTotalDaoI.query();
    }

    @Override
    public Page<ProjectDiggingMonthReportTotal> query(Specification<ProjectDiggingMonthReportTotal> spec) {
        return projectDiggingMonthReportTotalDaoI.query(spec);
    }

    @Override
    public Page<ProjectDiggingMonthReportTotal> query(Pageable pageable) {
        return projectDiggingMonthReportTotalDaoI.query(pageable);
    }

    @Override
    public Page<ProjectDiggingMonthReportTotal> query(Specification<ProjectDiggingMonthReportTotal> spec, Pageable pageable) {
        return projectDiggingMonthReportTotalDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectDiggingMonthReportTotal> getAll() {
        return projectDiggingMonthReportTotalDaoI.getAll();
    }

    @Override
    public List<ProjectDiggingMonthReportTotal> getByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectDiggingMonthReportTotalDaoI.getByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate) {
        projectDiggingMonthReportTotalDaoI.deleteByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public void setDeductionAndSubsidyAmount(Long id, Long deduction, Long subsidyAmount) {
        projectDiggingMonthReportTotalDaoI.setDeductionAndSubsidyAmount(id, deduction, subsidyAmount);
    }
}
