package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectDiggingPartCountGrandDaoI;
import com.seater.smartmining.entity.ProjectDiggingPartCount;
import com.seater.smartmining.entity.ProjectDiggingPartCountGrand;
import com.seater.smartmining.service.ProjectDiggingPartCountGrandServiceI;
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
 * @Date 2019/2/28 0028 17:45
 */
@Service
public class ProjectDiggingPartCountGrandServiceImpl implements ProjectDiggingPartCountGrandServiceI {

    @Autowired
    ProjectDiggingPartCountGrandDaoI projectDiggingPartCountGrandDaoI;

    @Override
    public ProjectDiggingPartCountGrand get(Long id) throws IOException {
        return projectDiggingPartCountGrandDaoI.get(id);
    }

    @Override
    public ProjectDiggingPartCountGrand save(ProjectDiggingPartCountGrand log) throws JsonProcessingException {
        return projectDiggingPartCountGrandDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectDiggingPartCountGrandDaoI.delete(id);
    }

    @Override
    public Page<ProjectDiggingPartCountGrand> query() {
        return projectDiggingPartCountGrandDaoI.query();
    }

    @Override
    public Page<ProjectDiggingPartCountGrand> query(Specification<ProjectDiggingPartCountGrand> spec) {
        return projectDiggingPartCountGrandDaoI.query(spec);
    }

    @Override
    public Page<ProjectDiggingPartCountGrand> query(Pageable pageable) {
        return projectDiggingPartCountGrandDaoI.query(pageable);
    }

    @Override
    public Page<ProjectDiggingPartCountGrand> query(Specification<ProjectDiggingPartCountGrand> spec, Pageable pageable) {
        return projectDiggingPartCountGrandDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectDiggingPartCountGrand> getAll() {
        return projectDiggingPartCountGrandDaoI.getAll();
    }

    @Override
    public List<ProjectDiggingPartCountGrand> getAllByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machineId) {
        return projectDiggingPartCountGrandDaoI.getAllByProjectIdAndReportDateAndMachineId(projectId, reportDate, machineId);
    }

    @Override
    public void deleteByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machineId) {
        projectDiggingPartCountGrandDaoI.deleteByProjectIdAndReportDateAndMachineId(projectId, reportDate, machineId);
    }

    @Override
    public ProjectDiggingPartCountGrand getAllByProjectIdAndTotalId(Long projectId, Long totalId) {
        return projectDiggingPartCountGrandDaoI.getAllByProjectIdAndTotalId(projectId, totalId);
    }
}
