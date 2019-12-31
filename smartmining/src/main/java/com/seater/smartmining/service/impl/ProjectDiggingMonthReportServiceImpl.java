package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectDiggingMonthReportDaoI;
import com.seater.smartmining.entity.ProjectDiggingMonthReport;
import com.seater.smartmining.service.ProjectDiggingMonthReportServiceI;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @Date 2019/1/28 0028 18:40
 */
@Service
public class ProjectDiggingMonthReportServiceImpl implements ProjectDiggingMonthReportServiceI {

    @Autowired
    private ProjectDiggingMonthReportDaoI projectDiggingMonthReportDaoI;

    @Override
    public ProjectDiggingMonthReport get(Long id) throws IOException {
        return projectDiggingMonthReportDaoI.get(id);
    }

    @Override
    public ProjectDiggingMonthReport save(ProjectDiggingMonthReport log) throws IOException {
        return projectDiggingMonthReportDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectDiggingMonthReportDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectDiggingMonthReportDaoI.delete(ids);
    }

    @Override
    public Page<ProjectDiggingMonthReport> query() {
        return projectDiggingMonthReportDaoI.query();
    }

    @Override
    public Page<ProjectDiggingMonthReport> query(Specification<ProjectDiggingMonthReport> spec) {
        return projectDiggingMonthReportDaoI.query(spec);
    }

    @Override
    public Page<ProjectDiggingMonthReport> query(Pageable pageable) {
        return projectDiggingMonthReportDaoI.query(pageable);
    }

    @Override
    public Page<ProjectDiggingMonthReport> query(Specification<ProjectDiggingMonthReport> spec, Pageable pageable) {
        return projectDiggingMonthReportDaoI.query(spec,pageable);
    }

    @Override
    public List<ProjectDiggingMonthReport> getAll() {
        return projectDiggingMonthReportDaoI.getAll();
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDay) {
        projectDiggingMonthReportDaoI.deleteByProjectIdAndReportDate(projectId, reportDay);
    }

    @Override
    public List<ProjectDiggingMonthReport> getAllByProjectId(Long projectId) {
        return projectDiggingMonthReportDaoI.getAllByProjectId(projectId);
    }

    @Override
    public List<ProjectDiggingMonthReport> getByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectDiggingMonthReportDaoI.getByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public List<ProjectDiggingMonthReport> getByTotalId(Long totalId) {
        return projectDiggingMonthReportDaoI.getByTotalId(totalId);
    }

    @Override
    public void setDeductionAndSubsidyAmount(Long id, Long deduction, Long subsidyAmount, Long workTotalAmount) {
        projectDiggingMonthReportDaoI.setDeductionAndSubsidyAmount(id, deduction, subsidyAmount, workTotalAmount);
    }

    @Override
    public List<ProjectDiggingMonthReport> getByTotalIdAndOwnerId(Long totalId, Long ownerId) {
        return null;
    }

    @Override
    public List<ProjectDiggingMonthReport> saveAll(List<ProjectDiggingMonthReport> saveList) {
        return projectDiggingMonthReportDaoI.saveAll(saveList);
    }
}
