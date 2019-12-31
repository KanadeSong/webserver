package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectDiggingPartCountTotalDaoI;
import com.seater.smartmining.entity.ProjectDiggingPartCountTotal;
import com.seater.smartmining.service.ProjectDiggingPartCountTotalServiceI;
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
 * @Date 2019/2/28 0028 15:51
 */
@Service
public class ProjectDiggingPartCountTotalServiceImpl implements ProjectDiggingPartCountTotalServiceI {

    @Autowired
    ProjectDiggingPartCountTotalDaoI projectDiggingPartCountTotalDaoI;

    @Override
    public ProjectDiggingPartCountTotal get(Long id) throws IOException {
        return projectDiggingPartCountTotalDaoI.get(id);
    }

    @Override
    public ProjectDiggingPartCountTotal save(ProjectDiggingPartCountTotal log) throws JsonProcessingException {
        return projectDiggingPartCountTotalDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectDiggingPartCountTotalDaoI.delete(id);
    }

    @Override
    public Page<ProjectDiggingPartCountTotal> query() {
        return projectDiggingPartCountTotalDaoI.query();
    }

    @Override
    public Page<ProjectDiggingPartCountTotal> query(Specification<ProjectDiggingPartCountTotal> spec) {
        return projectDiggingPartCountTotalDaoI.query(spec);
    }

    @Override
    public Page<ProjectDiggingPartCountTotal> query(Pageable pageable) {
        return projectDiggingPartCountTotalDaoI.query(pageable);
    }

    @Override
    public Page<ProjectDiggingPartCountTotal> query(Specification<ProjectDiggingPartCountTotal> spec, Pageable pageable) {
        return projectDiggingPartCountTotalDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectDiggingPartCountTotal> getAll() {
        return projectDiggingPartCountTotalDaoI.getAll();
    }

    @Override
    public List<ProjectDiggingPartCountTotal> getAllByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machine) {
        return projectDiggingPartCountTotalDaoI.getAllByProjectIdAndReportDateAndMachineId(projectId, reportDate, machine);
    }

    @Override
    public void deleteByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machineId) {
        projectDiggingPartCountTotalDaoI.deleteByProjectIdAndReportDateAndMachineId(projectId, reportDate, machineId);
    }
}
