package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectCarMaterialDaoI;
import com.seater.smartmining.entity.ProjectCarMaterial;
import com.seater.smartmining.service.ProjectCarMaterialServiceI;
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
public class ProjectCarMaterialServiceImpl implements ProjectCarMaterialServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectCarMaterialDaoI projectCarMaterialDaoI;

    @Override
    public ProjectCarMaterial get(Long id) throws IOException{
        return projectCarMaterialDaoI.get(id);
    }

    @Override
    public ProjectCarMaterial save(ProjectCarMaterial log) throws IOException{
        return projectCarMaterialDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectCarMaterialDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectCarMaterialDaoI.delete(ids);
    }

    @Override
    public Page<ProjectCarMaterial> query(Pageable pageable) {
        return projectCarMaterialDaoI.query(pageable);
    }

    @Override
    public Page<ProjectCarMaterial> query() {
        return projectCarMaterialDaoI.query();
    }

    @Override
    public Page<ProjectCarMaterial> query(Specification<ProjectCarMaterial> spec) {
        return projectCarMaterialDaoI.query(spec);
    }

    @Override
    public Page<ProjectCarMaterial> query(Specification<ProjectCarMaterial> spec, Pageable pageable) {
        return projectCarMaterialDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectCarMaterial> getAll() {
        return projectCarMaterialDaoI.getAll();
    }

    @Override
    public List<ProjectCarMaterial> getByProjectIdOrderById(Long projectId) {
            return projectCarMaterialDaoI.getByProjectIdOrderById(projectId);
    }

    @Override
    public ProjectCarMaterial getPayableByProjectIdAndDistance(Long projectId, Long distance) {
        return projectCarMaterialDaoI.getPayableByProjectIdAndDistance(projectId, distance);
    }

    @Override
    public Long getMaxDistanceByProjectId(Long projectId) {
        return projectCarMaterialDaoI.getMaxDistanceByProjectId(projectId);
    }

    @Override
    public Long getOverDistancePriceByProjectId(Long projectId) {
        return projectCarMaterialDaoI.getOverDistancePriceByProjectId(projectId);
    }
}
