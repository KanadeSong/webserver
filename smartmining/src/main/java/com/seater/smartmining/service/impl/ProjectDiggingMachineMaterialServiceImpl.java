package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectDiggingMachineMaterialDaoI;
import com.seater.smartmining.entity.ProjectDiggingMachineMaterial;
import com.seater.smartmining.service.ProjectDiggingMachineMaterialServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ProjectDiggingMachineMaterialServiceImpl implements ProjectDiggingMachineMaterialServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectDiggingMachineMaterialDaoI projectDiggingMachineMaterialDaoI;

    @Override
    public ProjectDiggingMachineMaterial get(Long id) throws IOException{
        return projectDiggingMachineMaterialDaoI.get(id);
    }

    @Override
    public ProjectDiggingMachineMaterial save(ProjectDiggingMachineMaterial log) throws IOException{
        return projectDiggingMachineMaterialDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectDiggingMachineMaterialDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectDiggingMachineMaterialDaoI.delete(ids);
    }

    @Override
    public Page<ProjectDiggingMachineMaterial> query(Pageable pageable) {
        return projectDiggingMachineMaterialDaoI.query(pageable);
    }

    @Override
    public Page<ProjectDiggingMachineMaterial> query() {
        return projectDiggingMachineMaterialDaoI.query();
    }

    @Override
    public Page<ProjectDiggingMachineMaterial> query(Specification<ProjectDiggingMachineMaterial> spec) {
        return projectDiggingMachineMaterialDaoI.query(spec);
    }

    @Override
    public Page<ProjectDiggingMachineMaterial> query(Specification<ProjectDiggingMachineMaterial> spec, Pageable pageable) {
        return projectDiggingMachineMaterialDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectDiggingMachineMaterial> getAll() {
        return projectDiggingMachineMaterialDaoI.getAll();
    }

    @Override
    public List<ProjectDiggingMachineMaterial> getByProjectIdOrderById(Long projectId) {
        return projectDiggingMachineMaterialDaoI.getByProjectIdOrderById(projectId);
    }

    @Override
    public ProjectDiggingMachineMaterial getByProjectIdAndMaterialId(Long projectId, Long materialId) {
        List<ProjectDiggingMachineMaterial> projectDiggingMachineMaterialList = projectDiggingMachineMaterialDaoI.getByProjectIdAndMaterialId(projectId,materialId);
        if(projectDiggingMachineMaterialList.size() > 0){
            return projectDiggingMachineMaterialList.get(0);
        }
        return null;
    }
}
