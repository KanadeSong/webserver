package com.seater.smartmining.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectScheduleLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/7/23 0023 10:13
 */
public interface ProjectScheduleLogServiceI {

    ProjectScheduleLog get(Long id) throws IOException;
    ProjectScheduleLog save(ProjectScheduleLog log) throws JsonProcessingException;
    void delete(Long id);
    Page<ProjectScheduleLog> query();
    Page<ProjectScheduleLog> query(Specification<ProjectScheduleLog> spec);
    Page<ProjectScheduleLog> query(Pageable pageable);
    Page<ProjectScheduleLog> query(Specification<ProjectScheduleLog> spec, Pageable pageable);
    List<ProjectScheduleLog> getAll();
}
