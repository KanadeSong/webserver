package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectDiggingDayReportTotalDaoI;
import com.seater.smartmining.entity.ProjectDiggingDayReport;
import com.seater.smartmining.entity.ProjectDiggingDayReportTotal;
import com.seater.smartmining.service.ProjectDiggingDayReportTotalServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/2/15 0015 15:32
 */
@Service
public class ProjectDiggingDayReportTotalServiceImpl implements ProjectDiggingDayReportTotalServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectDiggingDayReportTotalDaoI projectDiggingDayReportTotalDaoI;

    @Override
    public ProjectDiggingDayReportTotal get(Long id) throws IOException {
        return projectDiggingDayReportTotalDaoI.get(id);
    }

    @Override
    public ProjectDiggingDayReportTotal save(ProjectDiggingDayReportTotal log) throws JsonProcessingException {
        return projectDiggingDayReportTotalDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectDiggingDayReportTotalDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectDiggingDayReportTotalDaoI.delete(ids);
    }

    @Override
    public Page<ProjectDiggingDayReportTotal> query() {
        return projectDiggingDayReportTotalDaoI.query();
    }

    @Override
    public List<ProjectDiggingDayReportTotal> getAll() {
        return projectDiggingDayReportTotalDaoI.getAll();
    }

    @Override
    public List<ProjectDiggingDayReportTotal> getByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectDiggingDayReportTotalDaoI.getByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate) {
        projectDiggingDayReportTotalDaoI.deleteByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public List<ProjectDiggingDayReportTotal> getAllByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectDiggingDayReportTotalDaoI.getAllByProjectIdAndTime(projectId, startTime, endTime);
    }
}
