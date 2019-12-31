package com.seater.smartmining.dao;

import com.seater.smartmining.entity.Project;
import com.seater.smartmining.entity.ProjectWorkTimePoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.sql.Time;
import java.util.List;

public interface ProjectDaoI {
     Project get(Long id) throws IOException;
     Project save(Project log) throws IOException;
     void delete(Long id);
     void delete(List<Long> ids);
     Page<Project> query();
     Page<Project> query(Specification<Project> spec);
     Page<Project> query(Pageable pageable);
     Page<Project> query(Specification<Project> spec, Pageable pageable);
     List<Project> getAll();
     void setWorkTime(Long id, Time earlyStart, ProjectWorkTimePoint earlyEndPoint, Time earlyEnd, ProjectWorkTimePoint nightStartPoint, Time nightStart, ProjectWorkTimePoint nightEndPoint, Time nightEnd);
     Page<Project> findByUserId(Long userId, Pageable pageable);
}