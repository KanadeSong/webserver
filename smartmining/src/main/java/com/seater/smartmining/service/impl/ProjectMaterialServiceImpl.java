package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectMaterialDaoI;
import com.seater.smartmining.entity.ProjectMaterial;
import com.seater.smartmining.service.ProjectMaterialServiceI;
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
public class ProjectMaterialServiceImpl implements ProjectMaterialServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectMaterialDaoI projectMaterialDaoI;

    @Override
    public ProjectMaterial get(Long id) throws IOException{
        return projectMaterialDaoI.get(id);
    }

    @Override
    public ProjectMaterial save(ProjectMaterial log) throws IOException{
        return projectMaterialDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectMaterialDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectMaterialDaoI.delete(ids);
    }

    @Override
    public Page<ProjectMaterial> query(Pageable pageable) {
        return projectMaterialDaoI.query(pageable);
    }

    @Override
    public Page<ProjectMaterial> query() {
        return projectMaterialDaoI.query();
    }

    @Override
    public Page<ProjectMaterial> query(Specification<ProjectMaterial> spec) {
        return projectMaterialDaoI.query(spec);
    }

    @Override
    public Page<ProjectMaterial> query(Specification<ProjectMaterial> spec, Pageable pageable) {
        return projectMaterialDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectMaterial> getAll() {
        return projectMaterialDaoI.getAll();
    }

    @Override
    public List<ProjectMaterial> getByProjectIdOrderById(Long projectId) {
            return projectMaterialDaoI.getByProjectIdOrderById(projectId);
    }

    @Override
    public ProjectMaterial getByProjectIdAndName(Long projectId, String name) {
        return projectMaterialDaoI.getByProjectIdAndName(projectId, name);
    }
}
