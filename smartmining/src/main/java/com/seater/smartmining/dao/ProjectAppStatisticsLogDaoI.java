package com.seater.smartmining.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectAppStatisticsLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/4/11 0011 16:40
 */
public interface ProjectAppStatisticsLogDaoI {

    ProjectAppStatisticsLog get(Long id) throws IOException;
    ProjectAppStatisticsLog save(ProjectAppStatisticsLog log) throws JsonProcessingException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectAppStatisticsLog> query();
    Page<ProjectAppStatisticsLog> query(Specification<ProjectAppStatisticsLog> spec);
    Page<ProjectAppStatisticsLog> query(Pageable pageable);
    Page<ProjectAppStatisticsLog> query(Specification<ProjectAppStatisticsLog> spec, Pageable pageable);
    List<ProjectAppStatisticsLog> getAll();
    void deleteByProjectAndReportDate(Long projectId, Date reportDate);
    List<ProjectAppStatisticsLog> getAllByProjectIdAndReportDate(Long projectId, Date reportDate);
}
