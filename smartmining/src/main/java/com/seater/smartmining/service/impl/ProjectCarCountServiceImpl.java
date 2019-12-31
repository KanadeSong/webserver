package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectCarCountDaoI;
import com.seater.smartmining.entity.ProjectCarCount;
import com.seater.smartmining.service.ProjectCarCountServiceI;
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
 * @Date 2019/8/15 0015 14:21
 */
@Service
public class ProjectCarCountServiceImpl implements ProjectCarCountServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectCarCountDaoI projectCarCountDaoI;

    @Override
    public ProjectCarCount get(Long id) throws IOException {
        return projectCarCountDaoI.get(id);
    }

    @Override
    public ProjectCarCount save(ProjectCarCount log) throws IOException {
        return projectCarCountDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectCarCountDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectCarCountDaoI.delete(ids);
    }

    @Override
    public Page<ProjectCarCount> query() {
        return projectCarCountDaoI.query();
    }

    @Override
    public Page<ProjectCarCount> query(Specification<ProjectCarCount> spec) {
        return projectCarCountDaoI.query(spec);
    }

    @Override
    public Page<ProjectCarCount> query(Pageable pageable) {
        return projectCarCountDaoI.query(pageable);
    }

    @Override
    public Page<ProjectCarCount> query(Specification<ProjectCarCount> spec, Pageable pageable) {
        return projectCarCountDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectCarCount> getAll() {
        return projectCarCountDaoI.getAll();
    }

    @Override
    public ProjectCarCount getAllByProjectIdAndCarCodeAndDateIdentificationAndShiftsAndCarType(Long projectId, String carCode, Date date, Integer shifts, Integer carType) {
        return projectCarCountDaoI.getAllByProjectIdAndCarCodeAndDateIdentificationAndShiftsAndCarType(projectId, carCode, date, shifts, carType);
    }

    @Override
    public List<ProjectCarCount> getAllByProjectIdAndDateIdentificationAndShiftsAndCarType(Long projectId, Date date, Integer shift, Integer carType) {
        return projectCarCountDaoI.getAllByProjectIdAndDateIdentificationAndShiftsAndCarType(projectId, date, shift, carType);
    }
}
