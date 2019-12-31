package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectDiggingPartCountDaoI;
import com.seater.smartmining.entity.ProjectDiggingPartCount;
import com.seater.smartmining.service.ProjectDiggingPartCountServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/2/28 0028 16:09
 */
@Service
public class ProjectDiggingPartCountServiceImpl implements ProjectDiggingPartCountServiceI {

    @Autowired
    ProjectDiggingPartCountDaoI projectDiggingPartCountDaoI;

    @Override
    public ProjectDiggingPartCount get(Long id) throws IOException {
        return projectDiggingPartCountDaoI.get(id);
    }

    @Override
    public ProjectDiggingPartCount save(ProjectDiggingPartCount log) throws JsonProcessingException {
        return projectDiggingPartCountDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectDiggingPartCountDaoI.delete(id);
    }

    @Override
    public Page<ProjectDiggingPartCount> query() {
        return projectDiggingPartCountDaoI.query();
    }

    @Override
    public Page<ProjectDiggingPartCount> query(Specification<ProjectDiggingPartCount> spec) {
        return projectDiggingPartCountDaoI.query(spec);
    }

    @Override
    public Page<ProjectDiggingPartCount> query(Pageable pageable) {
        return projectDiggingPartCountDaoI.query(pageable);
    }

    @Override
    public Page<ProjectDiggingPartCount> query(Specification<ProjectDiggingPartCount> spec, Pageable pageable) {
        return projectDiggingPartCountDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectDiggingPartCount> getAll() {
        return projectDiggingPartCountDaoI.getAll();
    }

    @Override
    public List<ProjectDiggingPartCount> getByProjectIdAndTotalIdAndMachineId(Long projectId, Long totalId, Long machineId) {
        return projectDiggingPartCountDaoI.getByProjectIdAndTotalIdAndMachineId(projectId, totalId, machineId);
    }

    @Override
    public void deleteByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machineId) {
        projectDiggingPartCountDaoI.deleteByProjectIdAndReportDateAndMachineId(projectId, reportDate, machineId);
    }

    @Override
    public List<Map> getMachineIdByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectDiggingPartCountDaoI.getMachineIdByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getByProjectIdAndMachineIdAndTime(Long projectId, Long machineId, Date time) {
        return projectDiggingPartCountDaoI.getByProjectIdAndMachineIdAndTime(projectId, machineId, time);
    }
}
