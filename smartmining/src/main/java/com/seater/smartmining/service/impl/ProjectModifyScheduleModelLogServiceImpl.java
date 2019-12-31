package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectModifyScheduleModelLogDaoI;
import com.seater.smartmining.entity.ProjectModifyScheduleModelLog;
import com.seater.smartmining.service.ProjectModifyScheduleModelLogServiceI;
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
 * @Date 2019/11/15 0015 14:19
 */
@Service
public class ProjectModifyScheduleModelLogServiceImpl implements ProjectModifyScheduleModelLogServiceI {

    @Autowired
    private ProjectModifyScheduleModelLogDaoI projectModifyScheduleModelLogDaoI;

    @Override
    public ProjectModifyScheduleModelLog get(Long id) throws IOException {
        return projectModifyScheduleModelLogDaoI.get(id);
    }

    @Override
    public ProjectModifyScheduleModelLog save(ProjectModifyScheduleModelLog log) throws IOException {
        return projectModifyScheduleModelLogDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectModifyScheduleModelLogDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectModifyScheduleModelLogDaoI.delete(ids);
    }

    @Override
    public Page<ProjectModifyScheduleModelLog> query() {
        return projectModifyScheduleModelLogDaoI.query();
    }

    @Override
    public Page<ProjectModifyScheduleModelLog> query(Specification<ProjectModifyScheduleModelLog> spec) {
        return projectModifyScheduleModelLogDaoI.query(spec);
    }

    @Override
    public Page<ProjectModifyScheduleModelLog> query(Pageable pageable) {
        return projectModifyScheduleModelLogDaoI.query(pageable);
    }

    @Override
    public Page<ProjectModifyScheduleModelLog> query(Specification<ProjectModifyScheduleModelLog> spec, Pageable pageable) {
        return projectModifyScheduleModelLogDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectModifyScheduleModelLog> getAll() {
        return projectModifyScheduleModelLogDaoI.getAll();
    }
}
