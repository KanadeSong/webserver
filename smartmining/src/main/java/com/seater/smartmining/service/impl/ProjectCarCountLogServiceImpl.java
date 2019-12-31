package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectCarCountLogDaoI;
import com.seater.smartmining.entity.ProjectCarCountLog;
import com.seater.smartmining.service.ProjectCarCountLogServiceI;
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
 * @Date 2019/12/2 0002 14:48
 */
@Service
public class ProjectCarCountLogServiceImpl implements ProjectCarCountLogServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectCarCountLogDaoI projectCarCountLogDaoI;

    @Override
    public ProjectCarCountLog get(Long id) throws IOException {
        return projectCarCountLogDaoI.get(id);
    }

    @Override
    public ProjectCarCountLog save(ProjectCarCountLog log) throws IOException {
        return projectCarCountLogDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectCarCountLogDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectCarCountLogDaoI.delete(ids);
    }

    @Override
    public Page<ProjectCarCountLog> query() {
        return projectCarCountLogDaoI.query();
    }

    @Override
    public Page<ProjectCarCountLog> query(Specification<ProjectCarCountLog> spec) {
        return projectCarCountLogDaoI.query(spec);
    }

    @Override
    public Page<ProjectCarCountLog> query(Pageable pageable) {
        return projectCarCountLogDaoI.query(pageable);
    }

    @Override
    public Page<ProjectCarCountLog> query(Specification<ProjectCarCountLog> spec, Pageable pageable) {
        return projectCarCountLogDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectCarCountLog> getAll() {
        return projectCarCountLogDaoI.getAll();
    }
}
