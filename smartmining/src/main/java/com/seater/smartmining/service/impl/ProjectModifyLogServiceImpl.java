package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectModifyLogDaoI;
import com.seater.smartmining.entity.ProjectModifyLog;
import com.seater.smartmining.service.ProjectModifyLogServiceI;
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
 * @Date 2019/7/16 0016 11:12
 */
@Service
public class ProjectModifyLogServiceImpl implements ProjectModifyLogServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectModifyLogDaoI projectModifyLogDaoI;

    @Override
    public ProjectModifyLog get(Long id) throws IOException {
        return projectModifyLogDaoI.get(id);
    }

    @Override
    public ProjectModifyLog save(ProjectModifyLog log) throws IOException {
        return projectModifyLogDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectModifyLogDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectModifyLogDaoI.delete(ids);
    }

    @Override
    public Page<ProjectModifyLog> query() {
        return projectModifyLogDaoI.query();
    }

    @Override
    public Page<ProjectModifyLog> query(Specification<ProjectModifyLog> spec) {
        return projectModifyLogDaoI.query(spec);
    }

    @Override
    public Page<ProjectModifyLog> query(Pageable pageable) {
        return projectModifyLogDaoI.query(pageable);
    }

    @Override
    public Page<ProjectModifyLog> query(Specification<ProjectModifyLog> spec, Pageable pageable) {
        return projectModifyLogDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectModifyLog> getAll() {
        return projectModifyLogDaoI.getAll();
    }

    @Override
    public void batchSave(List<ProjectModifyLog> logList) {
        projectModifyLogDaoI.batchSave(logList);
    }
}
