package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectCarLoadMaterialSetDaoI;
import com.seater.smartmining.entity.ProjectCarLoadMaterialSet;
import com.seater.smartmining.service.ProjectCarLoadMaterialSetServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ProjectCarLoadMaterialSetServiceImpl implements ProjectCarLoadMaterialSetServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectCarLoadMaterialSetDaoI projectCarLoadMaterialSetDaoI;

    @Override
    public ProjectCarLoadMaterialSet get(Long id) throws IOException {
        return projectCarLoadMaterialSetDaoI.get(id);
    }

    @Override
    public ProjectCarLoadMaterialSet save(ProjectCarLoadMaterialSet log) throws IOException{
        return projectCarLoadMaterialSetDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectCarLoadMaterialSetDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectCarLoadMaterialSetDaoI.delete(ids);
    }

    @Override
    public Page<ProjectCarLoadMaterialSet> query(Pageable pageable) {
        return projectCarLoadMaterialSetDaoI.query(pageable);
    }

    @Override
    public Page<ProjectCarLoadMaterialSet> query() {
        return projectCarLoadMaterialSetDaoI.query();
    }

    @Override
    public Page<ProjectCarLoadMaterialSet> query(Specification<ProjectCarLoadMaterialSet> spec) {
        return projectCarLoadMaterialSetDaoI.query(spec);
    }

    @Override
    public Page<ProjectCarLoadMaterialSet> query(Specification<ProjectCarLoadMaterialSet> spec, Pageable pageable) {
        return projectCarLoadMaterialSetDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectCarLoadMaterialSet> getAll() {
        return projectCarLoadMaterialSetDaoI.getAll();
    }

    @Override
    public List<ProjectCarLoadMaterialSet> getByProjectIdAndCarIdOrderById(Long projectId, Long carId) {
        return projectCarLoadMaterialSetDaoI.getByProjectIdAndCarIdOrderById(projectId, carId);
    }

    @Override
    public ProjectCarLoadMaterialSet getByProjectIdAndCarIDAndMaterialId(Long projectId, Long carId, Long materialId) {
        return projectCarLoadMaterialSetDaoI.getByProjectIdAndCarIDAndMaterialId(projectId, carId, materialId);
    }
}
