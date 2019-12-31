package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectSlagSiteModifyLogDaoI;
import com.seater.smartmining.entity.ProjectSlagSiteModifyLog;
import com.seater.smartmining.service.ProjectSlagSiteModifyLogServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/8/15 17:56
 */
@Service
public class ProjectSlagSiteModifyLogServiceImpl implements ProjectSlagSiteModifyLogServiceI {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    ProjectSlagSiteModifyLogDaoI projectSlagSiteModifyLogDaoI;

    @Override
    public ProjectSlagSiteModifyLog get(Long id) throws IOException {
        return projectSlagSiteModifyLogDaoI.get(id);
    }

    @Override
    public ProjectSlagSiteModifyLog save(ProjectSlagSiteModifyLog log) throws IOException{
        return projectSlagSiteModifyLogDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectSlagSiteModifyLogDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectSlagSiteModifyLogDaoI.delete(ids);
    }

    @Override
    public Page<ProjectSlagSiteModifyLog> query(Pageable pageable) {
        return projectSlagSiteModifyLogDaoI.query(pageable);
    }

    @Override
    public Page<ProjectSlagSiteModifyLog> query() {
        return projectSlagSiteModifyLogDaoI.query();
    }

    @Override
    public Page<ProjectSlagSiteModifyLog> query(Specification<ProjectSlagSiteModifyLog> spec) {
        return projectSlagSiteModifyLogDaoI.query(spec);
    }

    @Override
    public Page<ProjectSlagSiteModifyLog> query(Specification<ProjectSlagSiteModifyLog> spec, Pageable pageable) {
        return projectSlagSiteModifyLogDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectSlagSiteModifyLog> getAll() {
        return projectSlagSiteModifyLogDaoI.getAll();
    }
}
