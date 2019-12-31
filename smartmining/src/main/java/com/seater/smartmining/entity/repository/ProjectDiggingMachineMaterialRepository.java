package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectCarMaterial;
import com.seater.smartmining.entity.ProjectDiggingMachineMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProjectDiggingMachineMaterialRepository extends JpaRepository<ProjectDiggingMachineMaterial, Long>, JpaSpecificationExecutor<ProjectDiggingMachineMaterial> {
    List<ProjectDiggingMachineMaterial> getByProjectIdOrderById(Long projectId);

    List<ProjectDiggingMachineMaterial> getByProjectIdAndMaterialId(Long projectId, Long materialId);
}
