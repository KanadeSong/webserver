package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectDiggingDayReportHistoryDaoI;
import com.seater.smartmining.entity.ProjectDiggingDayReportHistory;
import com.seater.smartmining.service.ProjectDiggingDayReportHistoryServiceI;
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
 * @Date 2019/6/11 0011 16:35
 */
@Service
public class ProjectDiggingDayReportHistoryServiceImpl implements ProjectDiggingDayReportHistoryServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectDiggingDayReportHistoryDaoI projectDiggingDayReportHistoryDaoI;

    @Override
    public ProjectDiggingDayReportHistory get(Long id) throws IOException {
        return projectDiggingDayReportHistoryDaoI.get(id);
    }

    @Override
    public ProjectDiggingDayReportHistory save(ProjectDiggingDayReportHistory log) throws JsonProcessingException {
        return projectDiggingDayReportHistoryDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectDiggingDayReportHistoryDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectDiggingDayReportHistoryDaoI.delete(ids);
    }

    @Override
    public Page<ProjectDiggingDayReportHistory> query() {
        return projectDiggingDayReportHistoryDaoI.query();
    }

    @Override
    public Page<ProjectDiggingDayReportHistory> query(Specification<ProjectDiggingDayReportHistory> spec) {
        return projectDiggingDayReportHistoryDaoI.query(spec);
    }

    @Override
    public Page<ProjectDiggingDayReportHistory> query(Pageable pageable) {
        return projectDiggingDayReportHistoryDaoI.query(pageable);
    }

    @Override
    public Page<ProjectDiggingDayReportHistory> query(Specification<ProjectDiggingDayReportHistory> spec, Pageable pageable) {
        return projectDiggingDayReportHistoryDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectDiggingDayReportHistory> getAll() {
        return projectDiggingDayReportHistoryDaoI.getAll();
    }

    @Override
    public ProjectDiggingDayReportHistory getAllByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectDiggingDayReportHistoryDaoI.getAllByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate) {
        projectDiggingDayReportHistoryDaoI.deleteByProjectIdAndReportDate(projectId, reportDate);
    }
}
