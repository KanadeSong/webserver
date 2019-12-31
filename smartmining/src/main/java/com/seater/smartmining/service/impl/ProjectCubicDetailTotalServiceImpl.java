package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectCubicDetailTotalDaoI;
import com.seater.smartmining.entity.ProjectCubicDetailTotal;
import com.seater.smartmining.service.ProjectCubicDetailTotalServiceI;
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
 * @Date 2019/3/4 0004 17:03
 */
@Service
public class ProjectCubicDetailTotalServiceImpl implements ProjectCubicDetailTotalServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectCubicDetailTotalDaoI projectCubicDetailTotalDaoI;

    @Override
    public ProjectCubicDetailTotal get(Long id) throws IOException {
        return projectCubicDetailTotalDaoI.get(id);
    }

    @Override
    public ProjectCubicDetailTotal save(ProjectCubicDetailTotal log) throws JsonProcessingException {
        return projectCubicDetailTotalDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectCubicDetailTotalDaoI.delete(id);
    }

    @Override
    public Page<ProjectCubicDetailTotal> query() {
        return projectCubicDetailTotalDaoI.query();
    }

    @Override
    public Page<ProjectCubicDetailTotal> query(Specification<ProjectCubicDetailTotal> spec) {
        return projectCubicDetailTotalDaoI.query(spec);
    }

    @Override
    public Page<ProjectCubicDetailTotal> query(Pageable pageable) {
        return projectCubicDetailTotalDaoI.query(pageable);
    }

    @Override
    public Page<ProjectCubicDetailTotal> query(Specification<ProjectCubicDetailTotal> spec, Pageable pageable) {
        return projectCubicDetailTotalDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectCubicDetailTotal> getAll() {
        return projectCubicDetailTotalDaoI.getAll();
    }

    @Override
    public void deleteByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machineId) {
        projectCubicDetailTotalDaoI.deleteByProjectIdAndReportDateAndMachineId(projectId, reportDate, machineId);
    }

    @Override
    public List<ProjectCubicDetailTotal> getAllByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machineId) {
        return projectCubicDetailTotalDaoI.getAllByProjectIdAndReportDateAndMachineId(projectId, reportDate, machineId);
    }
}
