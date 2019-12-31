package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectCarLoadMaterialSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProjectCarLoadMaterialSetRepository extends JpaRepository<ProjectCarLoadMaterialSet, Long>, JpaSpecificationExecutor<ProjectCarLoadMaterialSet> {
    List<ProjectCarLoadMaterialSet> getByProjectIdAndCarIDOrderById(Long projectId, Long carId);
    ProjectCarLoadMaterialSet getByProjectIdAndCarIDAndMaterialId(Long projectId, Long carId, Long materialId);
}
