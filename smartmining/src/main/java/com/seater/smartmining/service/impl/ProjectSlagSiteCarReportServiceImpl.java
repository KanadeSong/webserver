package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectSlagSiteCarReportDaoI;
import com.seater.smartmining.entity.ProjectSlagSiteCarReport;
import com.seater.smartmining.service.ProjectSlagSiteCarReportServiceI;
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
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/7/29 0029 15:18
 */
@Service
public class ProjectSlagSiteCarReportServiceImpl implements ProjectSlagSiteCarReportServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectSlagSiteCarReportDaoI projectSlagSiteCarReportDaoI;

    @Override
    public ProjectSlagSiteCarReport get(Long id) throws IOException {
        return projectSlagSiteCarReportDaoI.get(id);
    }

    @Override
    public ProjectSlagSiteCarReport save(ProjectSlagSiteCarReport log) throws IOException {
        return projectSlagSiteCarReportDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectSlagSiteCarReportDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectSlagSiteCarReportDaoI.delete(ids);
    }

    @Override
    public Page<ProjectSlagSiteCarReport> query() {
        return projectSlagSiteCarReportDaoI.query();
    }

    @Override
    public Page<ProjectSlagSiteCarReport> query(Specification<ProjectSlagSiteCarReport> spec) {
        return projectSlagSiteCarReportDaoI.query(spec);
    }

    @Override
    public Page<ProjectSlagSiteCarReport> query(Pageable pageable) {
        return projectSlagSiteCarReportDaoI.query(pageable);
    }

    @Override
    public Page<ProjectSlagSiteCarReport> query(Specification<ProjectSlagSiteCarReport> spec, Pageable pageable) {
        return projectSlagSiteCarReportDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectSlagSiteCarReport> getAll() {
        return projectSlagSiteCarReportDaoI.getAll();
    }

    @Override
    public void batchSave(List<ProjectSlagSiteCarReport> reportList) {
        projectSlagSiteCarReportDaoI.batchSave(reportList);
    }

    @Override
    public List<ProjectSlagSiteCarReport> getAllByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectSlagSiteCarReportDaoI.getAllByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate) {
        projectSlagSiteCarReportDaoI.deleteByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public List<ProjectSlagSiteCarReport> queryAll(Specification<ProjectSlagSiteCarReport> spec) {
        return projectSlagSiteCarReportDaoI.queryAll(spec);
    }
}
