package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectCarMaterial;
import com.seater.smartmining.entity.repository.ProjectCarMaterialRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

public interface ProjectCarMaterialDaoI {
     ProjectCarMaterial get(Long id) throws IOException;
     ProjectCarMaterial save(ProjectCarMaterial log) throws IOException;
     void delete(Long id);
     void delete(List<Long> ids);
     Page<ProjectCarMaterial> query();
     Page<ProjectCarMaterial> query(Specification<ProjectCarMaterial> spec);
     Page<ProjectCarMaterial> query(Pageable pageable);
     Page<ProjectCarMaterial> query(Specification<ProjectCarMaterial> spec, Pageable pageable);
     List<ProjectCarMaterial> getAll();
     List<ProjectCarMaterial> getByProjectIdOrderById(Long projectId);
     ProjectCarMaterial getPayableByProjectIdAndDistance(Long projectId, Long distance);
     Long getMaxDistanceByProjectId(Long projectId);
     Long getOverDistancePriceByProjectId(Long projectId);
}