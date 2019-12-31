package com.seater.smartmining.service;

import com.seater.smartmining.entity.ProjectLoadLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ProjectLoadLogServiceI {
    ProjectLoadLog get(Long id) throws IOException;
    ProjectLoadLog save(ProjectLoadLog log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectLoadLog> query();
    Page<ProjectLoadLog> query(Specification<ProjectLoadLog> spec);
    Page<ProjectLoadLog> query(Pageable pageable);
    Page<ProjectLoadLog> query(Specification<ProjectLoadLog> spec, Pageable pageable);
    List<ProjectLoadLog> getAll();
    Date getMaxUnloadDateByCarCode(String carCode);
    List<Map> getMachineCountByProjectIdAndTime(Long projectId, Date startTime, Date endTime);
}
