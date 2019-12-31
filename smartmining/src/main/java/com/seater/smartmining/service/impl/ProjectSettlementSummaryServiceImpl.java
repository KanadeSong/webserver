package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectSettlementSummaryDaoI;
import com.seater.smartmining.entity.ProjectSettlementSummary;
import com.seater.smartmining.service.ProjectSettlementSummaryServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
 * @Date 2019/3/2 0002 17:44
 */
@Service
public class ProjectSettlementSummaryServiceImpl implements ProjectSettlementSummaryServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectSettlementSummaryDaoI projectSettlementSummaryDaoI;

    @Override
    public ProjectSettlementSummary get(Long id) throws IOException {
        return projectSettlementSummaryDaoI.get(id);
    }

    @Override
    public ProjectSettlementSummary save(ProjectSettlementSummary log) throws JsonProcessingException {
        return projectSettlementSummaryDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectSettlementSummaryDaoI.delete(id);
    }

    @Override
    public Page<ProjectSettlementSummary> query() {
        return projectSettlementSummaryDaoI.query();
    }

    @Override
    public Page<ProjectSettlementSummary> query(Specification<ProjectSettlementSummary> spec) {
        return projectSettlementSummaryDaoI.query(spec);
    }

    @Override
    public Page<ProjectSettlementSummary> query(Pageable pageable) {
        return projectSettlementSummaryDaoI.query(pageable);
    }

    @Override
    public Page<ProjectSettlementSummary> query(Specification<ProjectSettlementSummary> spec, Pageable pageable) {
        return projectSettlementSummaryDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectSettlementSummary> getAll() {
        return projectSettlementSummaryDaoI.getAll();
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate, Long carId) {
        projectSettlementSummaryDaoI.deleteByProjectIdAndReportDate(projectId, reportDate, carId);
    }

    @Override
    public List<ProjectSettlementSummary> getByProjectIdAndTotalId(Long projectId, Long totalId, Date reportDate) {
        return projectSettlementSummaryDaoI.getByProjectIdAndTotalId(projectId, totalId, reportDate);
    }
}
