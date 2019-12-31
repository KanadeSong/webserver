package com.seater.smartmining.service;

import com.seater.smartmining.entity.ProjectDayReportPartDistance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ProjectDayReportPartDistanceServiceI {
    ProjectDayReportPartDistance get(Long id) throws IOException;
    ProjectDayReportPartDistance save(ProjectDayReportPartDistance log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectDayReportPartDistance> query();
    Page<ProjectDayReportPartDistance> query(Specification<ProjectDayReportPartDistance> spec);
    Page<ProjectDayReportPartDistance> query(Pageable pageable);
    Page<ProjectDayReportPartDistance> query(Specification<ProjectDayReportPartDistance> spec, Pageable pageable);
    List<ProjectDayReportPartDistance> getAll();
    List<ProjectDayReportPartDistance> getByReportIdOrderByDistance(Long reportId);
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate);
    List<Map> getByProjectIdAndReportId(Long projectId, Long reportId);
}
