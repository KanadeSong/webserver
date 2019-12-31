package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectErrorLogDaoI;
import com.seater.smartmining.entity.ProjectErrorLog;
import com.seater.smartmining.service.ProjectErrorLogServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
 * @Date 2019/5/22 0022 17:30
 */
@Service
public class ProjectErrorLogServiceImpl implements ProjectErrorLogServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectErrorLogDaoI projectErrorLogDaoI;

    @Override
    public ProjectErrorLog get(Long id) throws IOException {
        return projectErrorLogDaoI.get(id);
    }

    @Override
    public ProjectErrorLog save(ProjectErrorLog log) throws IOException {
        return projectErrorLogDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectErrorLogDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectErrorLogDaoI.delete(ids);
    }

    @Override
    public Page<ProjectErrorLog> query() {
        return projectErrorLogDaoI.query();
    }

    @Override
    public Page<ProjectErrorLog> query(Specification<ProjectErrorLog> spec) {
        return projectErrorLogDaoI.query(spec);
    }

    @Override
    public Page<ProjectErrorLog> query(Pageable pageable) {
        return projectErrorLogDaoI.query(pageable);
    }

    @Override
    public Page<ProjectErrorLog> query(Specification<ProjectErrorLog> spec, Pageable pageable) {
        return projectErrorLogDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectErrorLog> getAll() {
        return projectErrorLogDaoI.getAll();
    }
}
