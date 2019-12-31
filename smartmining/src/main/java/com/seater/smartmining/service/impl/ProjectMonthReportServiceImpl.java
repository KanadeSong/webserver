package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectMonthReportDaoI;
import com.seater.smartmining.entity.ProjectMonthReport;
import com.seater.smartmining.service.ProjectMonthReportServiceI;
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
 * @Date 2019/2/19 0019 10:47
 */
@Service
public class ProjectMonthReportServiceImpl implements ProjectMonthReportServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectMonthReportDaoI projectMonthReportDaoI;

    @Override
    public ProjectMonthReport get(Long id) throws IOException {
        return projectMonthReportDaoI.get(id);
    }

    @Override
    public ProjectMonthReport save(ProjectMonthReport log) throws IOException {
        return projectMonthReportDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectMonthReportDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectMonthReportDaoI.delete(ids);
    }

    @Override
    public Page<ProjectMonthReport> query() {
        return projectMonthReportDaoI.query();
    }

    @Override
    public Page<ProjectMonthReport> query(Specification<ProjectMonthReport> spec) {
        return projectMonthReportDaoI.query(spec);
    }

    @Override
    public Page<ProjectMonthReport> query(Pageable pageable) {
        return projectMonthReportDaoI.query(pageable);
    }

    @Override
    public Page<ProjectMonthReport> query(Specification<ProjectMonthReport> spec, Pageable pageable) {
        return projectMonthReportDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectMonthReport> getAll() {
        return projectMonthReportDaoI.getAll();
    }

    @Override
    public List<ProjectMonthReport> getByTotalId(Long totalId) {
        return projectMonthReportDaoI.getByTotalId(totalId);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDay) {
        projectMonthReportDaoI.deleteByProjectIdAndReportDate(projectId, reportDay);
    }

    @Override
    public void setDeductionAndSubsidyAmount(Long id, Long deduction, Long subsidyAmount) {
        projectMonthReportDaoI.setDeductionAndSubsidyAmount(id, deduction, subsidyAmount);
    }

    @Override
    public List<ProjectMonthReport> getByTotalIdAndCarIdIn(Long totalId, List<Long> carIds) {
        return projectMonthReportDaoI.getByTotalIdAndCarIdIn(totalId,carIds);
    }

    @Override
    public ProjectMonthReport getAllByProjectIdAndCarIdAndReportDate(Long projectId, Long carId, Date reportDate) {
        return projectMonthReportDaoI.getAllByProjectIdAndCarIdAndReportDate(projectId, carId, reportDate);
    }
}
