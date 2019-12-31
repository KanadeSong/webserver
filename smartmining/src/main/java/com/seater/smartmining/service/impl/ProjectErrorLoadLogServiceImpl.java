package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectErrorLoadLogDaoI;
import com.seater.smartmining.entity.ProjectErrorLoadLog;
import com.seater.smartmining.service.ProjectErrorLoadLogServiceI;
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
 * @Date 2019/11/1 0001 0:25
 */
@Service
public class ProjectErrorLoadLogServiceImpl implements ProjectErrorLoadLogServiceI {

    @Autowired
    private ProjectErrorLoadLogDaoI projectErrorLoadLogDaoI;
    @Override
    public ProjectErrorLoadLog get(Long id) throws IOException {
        return projectErrorLoadLogDaoI.get(id);
    }

    @Override
    public ProjectErrorLoadLog save(ProjectErrorLoadLog log) throws IOException {
        return projectErrorLoadLogDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectErrorLoadLogDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectErrorLoadLogDaoI.delete(ids);
    }

    @Override
    public Page<ProjectErrorLoadLog> query() {
        return projectErrorLoadLogDaoI.query();
    }

    @Override
    public Page<ProjectErrorLoadLog> query(Specification<ProjectErrorLoadLog> spec) {
        return projectErrorLoadLogDaoI.query(spec);
    }

    @Override
    public Page<ProjectErrorLoadLog> query(Pageable pageable) {
        return projectErrorLoadLogDaoI.query(pageable);
    }

    @Override
    public Page<ProjectErrorLoadLog> query(Specification<ProjectErrorLoadLog> spec, Pageable pageable) {
        return projectErrorLoadLogDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectErrorLoadLog> getAll() {
        return projectErrorLoadLogDaoI.getAll();
    }

    @Override
    public ProjectErrorLoadLog getAllByProjectIdAndCarCodeAndDateIdentificationAndShift(Long projectId, String carCode, Date dateIdentification, Integer shift) {
        return projectErrorLoadLogDaoI.getAllByProjectIdAndCarCodeAndDateIdentificationAndShift(projectId, carCode, dateIdentification, shift);
    }

    @Override
    public List<ProjectErrorLoadLog> getAllByProjectIdAndDateIdentificationAndShift(Long projectId, Date dateIdentification, Integer shift) {
        return projectErrorLoadLogDaoI.getAllByProjectIdAndDateIdentificationAndShift(projectId, dateIdentification, shift);
    }
}
