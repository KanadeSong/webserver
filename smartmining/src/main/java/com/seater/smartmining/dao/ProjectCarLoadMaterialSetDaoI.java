package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectCarLoadMaterialSet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

public interface ProjectCarLoadMaterialSetDaoI {
     ProjectCarLoadMaterialSet get(Long id) throws IOException;
     ProjectCarLoadMaterialSet save(ProjectCarLoadMaterialSet log) throws IOException;
     void delete(Long id);
     void delete(List<Long> ids);
     Page<ProjectCarLoadMaterialSet> query();
     Page<ProjectCarLoadMaterialSet> query(Specification<ProjectCarLoadMaterialSet> spec);
     Page<ProjectCarLoadMaterialSet> query(Pageable pageable);
     Page<ProjectCarLoadMaterialSet> query(Specification<ProjectCarLoadMaterialSet> spec, Pageable pageable);
     List<ProjectCarLoadMaterialSet> getAll();
     List<ProjectCarLoadMaterialSet>getByProjectIdAndCarIdOrderById(Long projectId, Long carId);
     ProjectCarLoadMaterialSet getByProjectIdAndCarIDAndMaterialId(Long projectId, Long carId, Long materialId);
}
