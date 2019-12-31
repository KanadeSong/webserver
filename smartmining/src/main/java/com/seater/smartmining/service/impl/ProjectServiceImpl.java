package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectDaoI;
import com.seater.smartmining.entity.Project;
import com.seater.smartmining.entity.ProjectWorkTimePoint;
import com.seater.smartmining.service.ProjectServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Time;
import java.util.*;

@Service
public class ProjectServiceImpl implements ProjectServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectDaoI projectDaoI;

    @Override
    public Project get(Long id) throws IOException {
        return projectDaoI.get(id);
    }

    @Override
    public Project save(Project log) throws IOException {
        return projectDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectDaoI.delete(ids);
    }

    @Override
    public Page<Project> query(Pageable pageable) {
        return projectDaoI.query(pageable);
    }

    @Override
    public Page<Project> query() {
        return projectDaoI.query();
    }

    @Override
    public Page<Project> query(Specification<Project> spec) {
        return projectDaoI.query(spec);
    }

    @Override
    public Page<Project> query(Specification<Project> spec, Pageable pageable) {
        return projectDaoI.query(spec, pageable);
    }

    @Override
    public List<Project> getAll() {
        return projectDaoI.getAll();
    }

    @Override
    public void setWorkTime(Long id, Time earlyStart, ProjectWorkTimePoint earlyEndPoint, Time earlyEnd, ProjectWorkTimePoint nightStartPoint, Time nightStart, ProjectWorkTimePoint nightEndPoint, Time nightEnd) {
        projectDaoI.setWorkTime(id, earlyStart, earlyEndPoint, earlyEnd, nightStartPoint, nightStart, nightEndPoint, nightEnd);
    }

    @Override
    public Page<Project> findByUserId(Long userId, Pageable pageable) {
        return projectDaoI.findByUserId(userId,pageable);
    }
}
