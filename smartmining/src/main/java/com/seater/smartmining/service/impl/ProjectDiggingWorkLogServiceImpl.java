package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectDiggingWorkLogDaoI;
import com.seater.smartmining.entity.ProjectDiggingWorkLog;
import com.seater.smartmining.service.ProjectDiggingWorkLogServiceI;
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
 * @Date 2019/9/18 0018 18:04
 */
@Service
public class ProjectDiggingWorkLogServiceImpl implements ProjectDiggingWorkLogServiceI {

    @Autowired
    private ProjectDiggingWorkLogDaoI projectDiggingWorkLogDaoI;

    @Override
    public ProjectDiggingWorkLog get(Long id) throws IOException {
        return projectDiggingWorkLogDaoI.get(id);
    }

    @Override
    public ProjectDiggingWorkLog save(ProjectDiggingWorkLog log) throws IOException {
        return projectDiggingWorkLogDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectDiggingWorkLogDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectDiggingWorkLogDaoI.delete(ids);
    }

    @Override
    public Page<ProjectDiggingWorkLog> query() {
        return projectDiggingWorkLogDaoI.query();
    }

    @Override
    public Page<ProjectDiggingWorkLog> query(Specification<ProjectDiggingWorkLog> spec) {
        return projectDiggingWorkLogDaoI.query(spec);
    }

    @Override
    public Page<ProjectDiggingWorkLog> query(Pageable pageable) {
        return projectDiggingWorkLogDaoI.query(pageable);
    }

    @Override
    public Page<ProjectDiggingWorkLog> query(Specification<ProjectDiggingWorkLog> spec, Pageable pageable) {
        return projectDiggingWorkLogDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectDiggingWorkLog> getAll() {
        return projectDiggingWorkLogDaoI.getAll();
    }
}
