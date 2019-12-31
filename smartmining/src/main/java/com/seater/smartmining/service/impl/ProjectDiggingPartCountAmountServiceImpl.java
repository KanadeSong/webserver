package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectDiggingPartCountAmountDaoI;
import com.seater.smartmining.entity.ProjectDiggingPartCountAmount;
import com.seater.smartmining.service.ProjectDiggingPartCountAmountServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/16 0016 11:56
 */
@Service
public class ProjectDiggingPartCountAmountServiceImpl implements ProjectDiggingPartCountAmountServiceI {

    @Autowired
    private ProjectDiggingPartCountAmountDaoI projectDiggingPartCountAmountDaoI;

    @Override
    public ProjectDiggingPartCountAmount get(Long id) throws IOException {
        return projectDiggingPartCountAmountDaoI.get(id);
    }

    @Override
    public ProjectDiggingPartCountAmount save(ProjectDiggingPartCountAmount log) throws JsonProcessingException {
        return projectDiggingPartCountAmountDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectDiggingPartCountAmountDaoI.delete(id);
    }

    @Override
    public Page<ProjectDiggingPartCountAmount> query() {
        return projectDiggingPartCountAmountDaoI.query();
    }

    @Override
    public Page<ProjectDiggingPartCountAmount> query(Specification<ProjectDiggingPartCountAmount> spec) {
        return projectDiggingPartCountAmountDaoI.query(spec);
    }

    @Override
    public Page<ProjectDiggingPartCountAmount> query(Pageable pageable) {
        return projectDiggingPartCountAmountDaoI.query(pageable);
    }

    @Override
    public Page<ProjectDiggingPartCountAmount> query(Specification<ProjectDiggingPartCountAmount> spec, Pageable pageable) {
        return projectDiggingPartCountAmountDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectDiggingPartCountAmount> getAll() {
        return projectDiggingPartCountAmountDaoI.getAll();
    }

    @Override
    public ProjectDiggingPartCountAmount getAllByProjectIdAndCountId(Long projectId, Long countId) {
        return projectDiggingPartCountAmountDaoI.getAllByProjectIdAndCountId(projectId, countId);
    }
}
