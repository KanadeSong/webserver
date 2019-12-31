package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectCarMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectCarMaterialRepository extends JpaRepository<ProjectCarMaterial, Long>, JpaSpecificationExecutor<ProjectCarMaterial> {
    List<ProjectCarMaterial> getByProjectIdOrderById(Long projectId);
    List<ProjectCarMaterial> getByProjectIdAndDistanceLessThanEqualOrderByDistanceDesc(Long projectId, Long distance);

    @Query("select max(distance) from ProjectCarMaterial  where projectId = ?1")
    List<Long> getMaxDistanceByProjectId(Long projectId);

    @Query("select price from ProjectCarMaterial where projectId = ?1 and distance = -1")
    List<Long> getOverDistancePriceByProjectId(Long projectId);
}
