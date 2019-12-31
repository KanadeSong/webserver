package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectMqttCardCountReportDaoI;
import com.seater.smartmining.entity.ProjectMqttCardCountReport;
import com.seater.smartmining.service.ProjectMqttCardCountReportServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/13 0013 12:49
 */
@Service
public class ProjectMqttCardCountReportServiceImpl implements ProjectMqttCardCountReportServiceI {

    @Autowired
    private ProjectMqttCardCountReportDaoI projectMqttCardCountReportDaoI;

    @Override
    public ProjectMqttCardCountReport get(Long id) throws IOException {
        return projectMqttCardCountReportDaoI.get(id);
    }

    @Override
    public ProjectMqttCardCountReport save(ProjectMqttCardCountReport log) throws JsonProcessingException {
        return projectMqttCardCountReportDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectMqttCardCountReportDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectMqttCardCountReportDaoI.delete(ids);
    }

    @Override
    public Page<ProjectMqttCardCountReport> query() {
        return projectMqttCardCountReportDaoI.query();
    }

    @Override
    public Page<ProjectMqttCardCountReport> query(Specification<ProjectMqttCardCountReport> spec) {
        return projectMqttCardCountReportDaoI.query(spec);
    }

    @Override
    public Page<ProjectMqttCardCountReport> query(Pageable pageable) {
        return projectMqttCardCountReportDaoI.query(pageable);
    }

    @Override
    public Page<ProjectMqttCardCountReport> query(Specification<ProjectMqttCardCountReport> spec, Pageable pageable) {
        return projectMqttCardCountReportDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectMqttCardCountReport> getAll() {
        return projectMqttCardCountReportDaoI.getAll();
    }

    @Override
    public void batchSave(List<ProjectMqttCardCountReport> saveList) {
        projectMqttCardCountReportDaoI.batchSave(saveList);
    }

    @Override
    public void deleteByProjectIdAndCreateTime(Long projectId, Date date, Integer shift) {
        projectMqttCardCountReportDaoI.deleteByProjectIdAndCreateTime(projectId, date, shift);
    }
}
