package com.seater.smartmining.service;

import com.seater.smartmining.entity.ProjectMaterial;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

public interface ProjectMaterialServiceI {
     ProjectMaterial get(Long id) throws IOException;
     ProjectMaterial save(ProjectMaterial log) throws IOException;
     void delete(Long id);
     void delete(List<Long> ids);
     Page<ProjectMaterial> query();
     Page<ProjectMaterial> query(Specification<ProjectMaterial> spec);
     Page<ProjectMaterial> query(Pageable pageable);
     Page<ProjectMaterial> query(Specification<ProjectMaterial> spec, Pageable pageable);
     List<ProjectMaterial> getAll();
     List<ProjectMaterial> getByProjectIdOrderById(Long projectId);
     ProjectMaterial getByProjectIdAndName(Long projectId, String name);
}