package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectTempSiteLogDaoI;
import com.seater.smartmining.entity.ProjectTempSiteLog;
import com.seater.smartmining.service.ProjectTempSiteLogServiceI;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @Date 2019/9/21 0021 11:36
 */
@Service
public class ProjectTempSiteLogServiceImpl implements ProjectTempSiteLogServiceI {

    @Autowired
    private ProjectTempSiteLogDaoI projectTempSiteLogDaoI;

    @Override
    public ProjectTempSiteLog get(Long id) throws IOException {
        return projectTempSiteLogDaoI.get(id);
    }

    @Override
    public ProjectTempSiteLog save(ProjectTempSiteLog log) throws IOException {
        return projectTempSiteLogDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectTempSiteLogDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectTempSiteLogDaoI.delete(ids);
    }

    @Override
    public Page<ProjectTempSiteLog> query() {
        return projectTempSiteLogDaoI.query();
    }

    @Override
    public Page<ProjectTempSiteLog> query(Specification<ProjectTempSiteLog> spec) {
        return projectTempSiteLogDaoI.query(spec);
    }

    @Override
    public Page<ProjectTempSiteLog> query(Pageable pageable) {
        return projectTempSiteLogDaoI.query(pageable);
    }

    @Override
    public Page<ProjectTempSiteLog> query(Specification<ProjectTempSiteLog> spec, Pageable pageable) {
        return projectTempSiteLogDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectTempSiteLog> queryAll(Specification<ProjectTempSiteLog> specification) {
        return projectTempSiteLogDaoI.queryAll(specification);
    }

    @Override
    public List<ProjectTempSiteLog> getAll() {
        return projectTempSiteLogDaoI.getAll();
    }

    @Override
    public Date getMaxUnloadDateByCarCode(String carCode) {
        return projectTempSiteLogDaoI.getMaxUnloadDateByCarCode(carCode);
    }
}
