package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectDayReportHistoryDaoI;
import com.seater.smartmining.entity.ProjectDayReportHistory;
import com.seater.smartmining.service.ProjectDayReportHistoryServiceI;
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
 * @Date 2019/5/7 0007 16:25
 */
@Service
public class ProjectDayReportHistoryServiceImpl implements ProjectDayReportHistoryServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectDayReportHistoryDaoI projectDayReportHistoryDaoI;

    @Override
    public ProjectDayReportHistory get(Long id) throws IOException {
        return projectDayReportHistoryDaoI.get(id);
    }

    @Override
    public ProjectDayReportHistory save(ProjectDayReportHistory log) throws JsonProcessingException {
        return projectDayReportHistoryDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectDayReportHistoryDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectDayReportHistoryDaoI.delete(ids);
    }

    @Override
    public Page<ProjectDayReportHistory> query() {
        return projectDayReportHistoryDaoI.query();
    }

    @Override
    public Page<ProjectDayReportHistory> query(Specification<ProjectDayReportHistory> spec) {
        return projectDayReportHistoryDaoI.query(spec);
    }

    @Override
    public Page<ProjectDayReportHistory> query(Pageable pageable) {
        return projectDayReportHistoryDaoI.query(pageable);
    }

    @Override
    public Page<ProjectDayReportHistory> query(Specification<ProjectDayReportHistory> spec, Pageable pageable) {
        return projectDayReportHistoryDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectDayReportHistory> getAll() {
        return projectDayReportHistoryDaoI.getAll();
    }

    @Override
    public ProjectDayReportHistory getAllByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectDayReportHistoryDaoI.getAllByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate) {
        projectDayReportHistoryDaoI.deleteByProjectIdAndReportDate(projectId, reportDate);
    }
}
