package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectAppStatisticsByCarDaoI;
import com.seater.smartmining.entity.ProjectAppStatisticsByCar;
import com.seater.smartmining.service.ProjectAppStatisticsByCarServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/6/9 0009 12:03
 */
@Service
public class ProjectAppStatisticsByCarServiceImpl implements ProjectAppStatisticsByCarServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectAppStatisticsByCarDaoI projectAppStatisticsByCarDaoI;

    @Override
    public ProjectAppStatisticsByCar get(Long id) throws IOException {
        return projectAppStatisticsByCarDaoI.get(id);
    }

    @Override
    public ProjectAppStatisticsByCar save(ProjectAppStatisticsByCar log) throws JsonProcessingException {
        return projectAppStatisticsByCarDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectAppStatisticsByCarDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectAppStatisticsByCarDaoI.delete(ids);
    }

    @Override
    public Page<ProjectAppStatisticsByCar> query() {
        return projectAppStatisticsByCarDaoI.query();
    }

    @Override
    public Page<ProjectAppStatisticsByCar> query(Specification<ProjectAppStatisticsByCar> spec) {
        return projectAppStatisticsByCarDaoI.query(spec);
    }

    @Override
    public Page<ProjectAppStatisticsByCar> query(Pageable pageable) {
        return projectAppStatisticsByCarDaoI.query(pageable);
    }

    @Override
    public Page<ProjectAppStatisticsByCar> query(Specification<ProjectAppStatisticsByCar> spec, Pageable pageable) {
        return projectAppStatisticsByCarDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectAppStatisticsByCar> getAll() {
        return projectAppStatisticsByCarDaoI.getAll();
    }

    @Override
    public void deleteByCreateDate(Date createDate, Long projectId) {
        projectAppStatisticsByCarDaoI.deleteByCreateDate(createDate, projectId);
    }

    @Override
    public ProjectAppStatisticsByCar getAllByProjectIdAndCarCodeAndShiftAndDate(Long projectId, String carCode, Integer value, Date date) {
        return projectAppStatisticsByCarDaoI.getAllByProjectIdAndCarCodeAndShiftAndDate(projectId, carCode, value, date);
    }

    @Override
    public List<ProjectAppStatisticsByCar> getAllByProjectIdAndShiftAndCreateDate(Long projectId, Integer value, Date date) {
        return projectAppStatisticsByCarDaoI.getAllByProjectIdAndShiftAndCreateDate(projectId, value, date);
    }

    @Override
    public void batchSave(List<ProjectAppStatisticsByCar> saveList) {
        projectAppStatisticsByCarDaoI.batchSave(saveList);
    }
}
