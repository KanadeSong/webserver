package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectCubicDetailDaoI;
import com.seater.smartmining.entity.ProjectCubicDetail;
import com.seater.smartmining.service.ProjectCubicDetailServiceI;
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
 * @Date 2019/3/4 0004 16:43
 */
@Service
public class ProjectCubicDetailServiceImpl implements ProjectCubicDetailServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectCubicDetailDaoI projectCubicDetailDaoI;

    @Override
    public ProjectCubicDetail get(Long id) throws IOException {
        return projectCubicDetailDaoI.get(id);
    }

    @Override
    public ProjectCubicDetail save(ProjectCubicDetail log) throws JsonProcessingException {
        return projectCubicDetailDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectCubicDetailDaoI.delete(id);
    }

    @Override
    public Page<ProjectCubicDetail> query() {
        return projectCubicDetailDaoI.query();
    }

    @Override
    public Page<ProjectCubicDetail> query(Specification<ProjectCubicDetail> spec) {
        return projectCubicDetailDaoI.query(spec);
    }

    @Override
    public Page<ProjectCubicDetail> query(Pageable pageable) {
        return projectCubicDetailDaoI.query(pageable);
    }

    @Override
    public Page<ProjectCubicDetail> query(Specification<ProjectCubicDetail> spec, Pageable pageable) {
        return projectCubicDetailDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectCubicDetail> getAll() {
        return projectCubicDetailDaoI.getAll();
    }

    @Override
    public void deleteByProjectIdAndCreateDateAndMachineId(Long projectId, Date createDate, Long machineId) {
        projectCubicDetailDaoI.deleteByProjectIdAndCreateDateAndMachineId(projectId, createDate, machineId);
    }

    @Override
    public List<ProjectCubicDetail> getAllByProjectIdAndTotalId(Long projectId, Long totalId, Date reportDate) {
        return projectCubicDetailDaoI.getAllByProjectIdAndTotalId(projectId, totalId, reportDate);
    }

    @Override
    public List<Map> getReportDateByProjectIdAndCarIdAndTotalId(Long projectId, Long machineId, Long totalId) {
        return projectCubicDetailDaoI.getReportDateByProjectIdAndCarIdAndTotalId(projectId, machineId, totalId);
    }

    @Override
    public List<Map> getByProjectIdAndReportDate(Long projectId, Date startTime, Date endTime) {
        return projectCubicDetailDaoI.getByProjectIdAndReportDate(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getByProjectIdAndReportDateAndMachineId(Long projectId, Date startTime, Date endTime, Long machineId) {
        return projectCubicDetailDaoI.getByProjectIdAndReportDateAndMachineId(projectId, startTime, endTime, machineId);
    }

    @Override
    public List<Map> getByProjectIdAndDate(Long projectId, Date startTime, Date endTime) {
        return projectCubicDetailDaoI.getByProjectIdAndDate(projectId, startTime, endTime);
    }
}
