package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectSettlementTotalDaoI;
import com.seater.smartmining.entity.ProjectSettlementTotal;
import com.seater.smartmining.service.ProjectSettlementTotalServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/2 0002 17:54
 */
@Service
public class ProjectSettlementTotalServiceImpl implements ProjectSettlementTotalServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectSettlementTotalDaoI projectSettlementTotalDaoI;

    @Override
    public ProjectSettlementTotal get(Long id) throws IOException {
        return projectSettlementTotalDaoI.get(id);
    }

    @Override
    public ProjectSettlementTotal save(ProjectSettlementTotal log) throws JsonProcessingException {
        return projectSettlementTotalDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectSettlementTotalDaoI.delete(id);
    }

    @Override
    public Page<ProjectSettlementTotal> query() {
        return projectSettlementTotalDaoI.query();
    }

    @Override
    public Page<ProjectSettlementTotal> query(Specification<ProjectSettlementTotal> spec) {
        return projectSettlementTotalDaoI.query(spec);
    }

    @Override
    public Page<ProjectSettlementTotal> query(Pageable pageable) {
        return projectSettlementTotalDaoI.query(pageable);
    }

    @Override
    public Page<ProjectSettlementTotal> query(Specification<ProjectSettlementTotal> spec, Pageable pageable) {
        return projectSettlementTotalDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectSettlementTotal> getAll() {
        return projectSettlementTotalDaoI.getAll();
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate, Long carId) {
        projectSettlementTotalDaoI.deleteByProjectIdAndReportDate(projectId, reportDate, carId);
    }

    @Override
    public List<ProjectSettlementTotal> getByProjectIdAndCarIdAndReportDate(Long projectId, Long carId, Date reportDate) {
        return projectSettlementTotalDaoI.getByProjectIdAndCarIdAndReportDate(projectId, carId, reportDate);
    }
}
