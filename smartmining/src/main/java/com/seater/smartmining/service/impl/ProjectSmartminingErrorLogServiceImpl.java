package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectSmartminingErrorLogDaoI;
import com.seater.smartmining.entity.ProjectSmartminingErrorLog;
import com.seater.smartmining.service.ProjectSmartminingErrorLogServiceI;
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
 * @Date 2019/9/19 0019 15:45
 */
@Service
public class ProjectSmartminingErrorLogServiceImpl implements ProjectSmartminingErrorLogServiceI {

    @Autowired
    private ProjectSmartminingErrorLogDaoI projectSmartminingErrorLogDaoI;

    @Override
    public ProjectSmartminingErrorLog get(Long id) throws IOException {
        return projectSmartminingErrorLogDaoI.get(id);
    }

    @Override
    public ProjectSmartminingErrorLog save(ProjectSmartminingErrorLog log) throws IOException {
        return projectSmartminingErrorLogDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectSmartminingErrorLogDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectSmartminingErrorLogDaoI.delete(ids);
    }

    @Override
    public Page<ProjectSmartminingErrorLog> query() {
        return projectSmartminingErrorLogDaoI.query();
    }

    @Override
    public Page<ProjectSmartminingErrorLog> query(Specification<ProjectSmartminingErrorLog> spec) {
        return projectSmartminingErrorLogDaoI.query(spec);
    }

    @Override
    public Page<ProjectSmartminingErrorLog> query(Pageable pageable) {
        return projectSmartminingErrorLogDaoI.query(pageable);
    }

    @Override
    public Page<ProjectSmartminingErrorLog> query(Specification<ProjectSmartminingErrorLog> spec, Pageable pageable) {
        return projectSmartminingErrorLogDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectSmartminingErrorLog> getAll() {
        return projectSmartminingErrorLogDaoI.getAll();
    }
}
