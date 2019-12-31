package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectCubicDetailElseDaoI;
import com.seater.smartmining.entity.ProjectCubicDetailElse;
import com.seater.smartmining.service.ProjectCubicDetailElseServiceI;
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
 * @Date 2019/3/5 0005 11:05
 */
@Service
public class ProjectCubicDetailElseServiceImpl implements ProjectCubicDetailElseServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectCubicDetailElseDaoI projectCubicDetailElseDaoI;

    @Override
    public ProjectCubicDetailElse get(Long id) throws IOException {
        return projectCubicDetailElseDaoI.get(id);
    }

    @Override
    public ProjectCubicDetailElse save(ProjectCubicDetailElse log) throws JsonProcessingException {
        return projectCubicDetailElseDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectCubicDetailElseDaoI.delete(id);
    }

    @Override
    public Page<ProjectCubicDetailElse> query() {
        return projectCubicDetailElseDaoI.query();
    }

    @Override
    public Page<ProjectCubicDetailElse> query(Specification<ProjectCubicDetailElse> spec) {
        return projectCubicDetailElseDaoI.query(spec);
    }

    @Override
    public Page<ProjectCubicDetailElse> query(Pageable pageable) {
        return projectCubicDetailElseDaoI.query(pageable);
    }

    @Override
    public Page<ProjectCubicDetailElse> query(Specification<ProjectCubicDetailElse> spec, Pageable pageable) {
        return projectCubicDetailElseDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectCubicDetailElse> getAll() {
        return projectCubicDetailElseDaoI.getAll();
    }

    @Override
    public void deleteByProjectIdAndCreateDateAndMachineId(Long projectId, Date createDate, Long machineId) {
        projectCubicDetailElseDaoI.deleteByProjectIdAndCreateDateAndMachineId(projectId, createDate, machineId);
    }

    @Override
    public List<ProjectCubicDetailElse> getAllByProjectIdAndTotalId(Long projectId, Long totalId, Date reportDate) {
        return projectCubicDetailElseDaoI.getAllByProjectIdAndTotalId(projectId, totalId, reportDate);
    }
}
