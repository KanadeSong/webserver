package com.seater.smartmining.service;

import com.seater.smartmining.entity.ProjectWorkTimeSet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

public interface ProjectWorkTimeSetServiceI {
     ProjectWorkTimeSet get(Long id) throws IOException;
     ProjectWorkTimeSet save(ProjectWorkTimeSet log) throws IOException;
     void delete(Long id);
     void delete(List<Long> ids);
     Page<ProjectWorkTimeSet> query();
     Page<ProjectWorkTimeSet> query(Specification<ProjectWorkTimeSet> spec);
     Page<ProjectWorkTimeSet> query(Pageable pageable);
     Page<ProjectWorkTimeSet> query(Specification<ProjectWorkTimeSet> spec, Pageable pageable);
     List<ProjectWorkTimeSet> getAll();
}
