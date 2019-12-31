package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProjectMaterialRepository extends JpaRepository<ProjectMaterial, Long>, JpaSpecificationExecutor<ProjectMaterial> {
    List<ProjectMaterial> getByProjectIdOrderById(Long projectId);

    ProjectMaterial getByProjectIdAndName(Long projectId, String name);
}
