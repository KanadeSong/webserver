package com.seater.smartmining.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectMqttCardCountReport;
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
 * @Date 2019/11/13 0013 12:49
 */
public interface ProjectMqttCardCountReportServiceI {

    ProjectMqttCardCountReport get(Long id) throws IOException;
    ProjectMqttCardCountReport save(ProjectMqttCardCountReport log) throws JsonProcessingException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectMqttCardCountReport> query();
    Page<ProjectMqttCardCountReport> query(Specification<ProjectMqttCardCountReport> spec);
    Page<ProjectMqttCardCountReport> query(Pageable pageable);
    Page<ProjectMqttCardCountReport> query(Specification<ProjectMqttCardCountReport> spec, Pageable pageable);
    List<ProjectMqttCardCountReport> getAll();
    void batchSave(List<ProjectMqttCardCountReport> saveList);
    void deleteByProjectIdAndCreateTime(Long projectId, Date date, Integer shift);
}
