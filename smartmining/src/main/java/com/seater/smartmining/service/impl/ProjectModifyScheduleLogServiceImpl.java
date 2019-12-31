package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectModifyScheduleLogDaoI;
import com.seater.smartmining.entity.ProjectModifyScheduleLog;
import com.seater.smartmining.service.ProjectModifyScheduleLogServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
 * @Date 2019/7/26 0026 10:54
 */
@Service
public class ProjectModifyScheduleLogServiceImpl implements ProjectModifyScheduleLogServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectModifyScheduleLogDaoI projectModifyScheduleLogDaoI;

    @Override
    public ProjectModifyScheduleLog get(Long id) throws IOException {
        return projectModifyScheduleLogDaoI.get(id);
    }

    @Override
    public ProjectModifyScheduleLog save(ProjectModifyScheduleLog log) throws IOException {
        return projectModifyScheduleLogDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectModifyScheduleLogDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectModifyScheduleLogDaoI.delete(ids);
    }

    @Override
    public Page<ProjectModifyScheduleLog> query() {
        return projectModifyScheduleLogDaoI.query();
    }

    @Override
    public Page<ProjectModifyScheduleLog> query(Specification<ProjectModifyScheduleLog> spec) {
        return projectModifyScheduleLogDaoI.query(spec);
    }

    @Override
    public Page<ProjectModifyScheduleLog> query(Pageable pageable) {
        return projectModifyScheduleLogDaoI.query(pageable);
    }

    @Override
    public Page<ProjectModifyScheduleLog> query(Specification<ProjectModifyScheduleLog> spec, Pageable pageable) {
        return projectModifyScheduleLogDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectModifyScheduleLog> getAll() {
        return projectModifyScheduleLogDaoI.getAll();
    }

    @Override
    public List<ProjectModifyScheduleLog> getAllByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift) {
        return projectModifyScheduleLogDaoI.getAllByProjectIdAndDateIdentificationAndShift(projectId, date, shift);
    }
}
