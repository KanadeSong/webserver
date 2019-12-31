package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectCarDaoI;
import com.seater.smartmining.entity.Project;
import com.seater.smartmining.entity.ProjectCar;
import com.seater.smartmining.entity.ProjectScheduleDetail;
import com.seater.smartmining.enums.ProjectCarStatus;
import com.seater.smartmining.service.ProjectCarServiceI;
import com.seater.smartmining.service.ProjectScheduleDetailServiceI;
import com.seater.smartmining.utils.schedule.AutoScheduleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ProjectCarServiceImpl implements ProjectCarServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectCarDaoI projectCarDaoI;
    @Autowired
    ProjectScheduleDetailServiceI projectScheduleDetailServiceI;

    @Override
    public ProjectCar get(Long id) throws IOException{
        return projectCarDaoI.get(id);
    }

    @Override
    public ProjectCar save(ProjectCar log) throws IOException{
        ProjectCar r = projectCarDaoI.save(log);
        projectScheduleDetailServiceI.initByCar(r);
        return r;
    }

    @Override
    public void delete(Long id) {
        projectCarDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectCarDaoI.delete(ids);
    }

    @Override
    public Page<ProjectCar> query(Pageable pageable) {
        return projectCarDaoI.query(pageable);
    }

    @Override
    public Page<ProjectCar> query() {
        return projectCarDaoI.query();
    }

    @Override
    public Page<ProjectCar> query(Specification<ProjectCar> spec) {
        return projectCarDaoI.query(spec);
    }

    @Override
    public Page<ProjectCar> query(Specification<ProjectCar> spec, Pageable pageable) {
        return projectCarDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectCar> getAll() {
        return projectCarDaoI.getAll();
    }

    @Override
    public List<ProjectCar> getByProjectIdOrderById(Long projectId) {
        return projectCarDaoI.getByProjectIdOrderById(projectId);
    }

    @Override
    public List<ProjectCar> getByProjectIdAndIsVaild(Long projectId, Boolean isVaild) {
        return projectCarDaoI.getByProjectIdAndIsVaild(projectId, isVaild);
    }

    @Override
    public Integer getCountByProjectId(Long projectId) {
        return projectCarDaoI.getCountByProjectId(projectId);
    }

    @Override
    public void setICCardByCarId(Long carId, String icCardNumber, Boolean icCardStatus) {
        projectCarDaoI.setICCardByProjectIdAndCarId(carId, icCardNumber, icCardStatus);
    }

    @Override
    public ProjectCar getByProjectIdAndCode(Long projectId, String code) {
        return projectCarDaoI.getByProjectIdAndCode(projectId, code);
    }

    @Override
    public Map getCarsCountByProjectId(Long projectId) {
        return projectCarDaoI.getCarsCountByProjectId(projectId);
    }

    @Override
    public List<ProjectCar> queryWx(Specification<ProjectCar> spec) {
        return projectCarDaoI.queryWx(spec);
    }

    @Override
    public List<ProjectCar> getAllByProjectIdAndSeleted(Long projectId, Boolean selected) {
        return projectCarDaoI.getAllByProjectIdAndSeleted(projectId, selected);
    }

    @Override
    public void batchSave(List<ProjectCar> projectCarList) {
        projectCarDaoI.batchSave(projectCarList);
    }

    @Override
    public List<String> getAllByProjectIdAndVaild(Long projectId, Boolean valid) {
        return projectCarDaoI.getAllByProjectIdAndVaild(projectId, valid);
    }

    @Override
    public void updateSeleted(boolean selected, List<String> carCodeList) {
        projectCarDaoI.updateSeleted(selected, carCodeList);
    }

}
