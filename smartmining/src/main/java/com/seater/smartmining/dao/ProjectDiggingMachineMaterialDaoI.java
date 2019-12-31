package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectDiggingMachineMaterial;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

public interface ProjectDiggingMachineMaterialDaoI {
     ProjectDiggingMachineMaterial get(Long id) throws IOException;
     ProjectDiggingMachineMaterial save(ProjectDiggingMachineMaterial log) throws IOException;
     void delete(Long id);
     void delete(List<Long> ids);
     Page<ProjectDiggingMachineMaterial> query();
     Page<ProjectDiggingMachineMaterial> query(Specification<ProjectDiggingMachineMaterial> spec);
     Page<ProjectDiggingMachineMaterial> query(Pageable pageable);
     Page<ProjectDiggingMachineMaterial> query(Specification<ProjectDiggingMachineMaterial> spec, Pageable pageable);
     List<ProjectDiggingMachineMaterial> getAll();
     List<ProjectDiggingMachineMaterial> getByProjectIdOrderById(Long projectId);
     List<ProjectDiggingMachineMaterial> getByProjectIdAndMaterialId(Long projectId, Long materialId);
}