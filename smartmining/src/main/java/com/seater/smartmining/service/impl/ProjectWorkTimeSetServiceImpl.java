package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectWorkTimeSetDaoI;
import com.seater.smartmining.entity.ProjectWorkTimeSet;
import com.seater.smartmining.service.ProjectWorkTimeSetServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ProjectWorkTimeSetServiceImpl implements ProjectWorkTimeSetServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectWorkTimeSetDaoI projectWorkTimeSetDaoI;

    @Override
    public ProjectWorkTimeSet get(Long id) throws IOException {
        return projectWorkTimeSetDaoI.get(id);
    }

    @Override
    public ProjectWorkTimeSet save(ProjectWorkTimeSet log) throws IOException{
        return projectWorkTimeSetDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectWorkTimeSetDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectWorkTimeSetDaoI.delete(ids);
    }

    @Override
    public Page<ProjectWorkTimeSet> query(Pageable pageable) {
        return projectWorkTimeSetDaoI.query(pageable);
    }

    @Override
    public Page<ProjectWorkTimeSet> query() {
        return projectWorkTimeSetDaoI.query();
    }

    @Override
    public Page<ProjectWorkTimeSet> query(Specification<ProjectWorkTimeSet> spec) {
        return projectWorkTimeSetDaoI.query(spec);
    }

    @Override
    public Page<ProjectWorkTimeSet> query(Specification<ProjectWorkTimeSet> spec, Pageable pageable) {
        return projectWorkTimeSetDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectWorkTimeSet> getAll() {
        return projectWorkTimeSetDaoI.getAll();
    }

/*    @Override
    public List<ProjectWorkTimeSet> getByProjectId(Long projectId) {
        return projectWorkTimeSetDaoI.getByProjectId(projectId);
    }*/
}
