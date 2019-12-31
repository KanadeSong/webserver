package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectSettlementDetailDaoI;
import com.seater.smartmining.entity.ProjectSettlementDetail;
import com.seater.smartmining.service.ProjectSettlementDetailServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
 * @Date 2019/3/2 0002 14:20
 */
@Service
public class ProjectSettlementDetailServiceImpl implements ProjectSettlementDetailServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectSettlementDetailDaoI projectSettlementDetailDaoI;

    @Override
    public ProjectSettlementDetail get(Long id) throws IOException {
        return projectSettlementDetailDaoI.get(id);
    }

    @Override
    public ProjectSettlementDetail save(ProjectSettlementDetail log) throws JsonProcessingException {
        return projectSettlementDetailDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectSettlementDetailDaoI.delete(id);
    }

    @Override
    public Page<ProjectSettlementDetail> query() {
        return projectSettlementDetailDaoI.query();
    }

    @Override
    public Page<ProjectSettlementDetail> query(Specification<ProjectSettlementDetail> spec) {
        return projectSettlementDetailDaoI.query(spec);
    }

    @Override
    public Page<ProjectSettlementDetail> query(Pageable pageable) {
        return projectSettlementDetailDaoI.query(pageable);
    }

    @Override
    public Page<ProjectSettlementDetail> query(Specification<ProjectSettlementDetail> spec, Pageable pageable) {
        return projectSettlementDetailDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectSettlementDetail> getAll() {
        return projectSettlementDetailDaoI.getAll();
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate, Long carId) {
        projectSettlementDetailDaoI.deleteByProjectIdAndReportDate(projectId, reportDate, carId);
    }

    @Override
    public List<ProjectSettlementDetail> getByProjectIdAndTotalId(Long projectId, Long totalId, Date reportDate) {
        return projectSettlementDetailDaoI.getByProjectIdAndTotalId(projectId, totalId, reportDate);
    }

    @Override
    public List<Map> getReportDateByProjectIdAndCarIdAndTotalId(Long projectId, Long carId, Long totalId) {
        return projectSettlementDetailDaoI.getReportDateByProjectIdAndCarIdAndTotalId(projectId, carId, totalId);
    }

    @Override
    public List<Map> getTotalInfoByTotalId(Long totalId) {
        return projectSettlementDetailDaoI.getTotalInfoByTotalId(totalId);
    }
}
