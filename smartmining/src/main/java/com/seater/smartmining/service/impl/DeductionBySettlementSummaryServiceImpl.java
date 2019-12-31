package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.DeductionBySettlementSummaryDaoI;
import com.seater.smartmining.entity.DeductionBySettlementSummary;
import com.seater.smartmining.service.DeductionBySettlementSummaryServiceI;
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
 * @Date 2019/5/20 0020 10:36
 */
@Service
public class DeductionBySettlementSummaryServiceImpl implements DeductionBySettlementSummaryServiceI {

    @Autowired
    DeductionBySettlementSummaryDaoI deductionBySettlementSummaryDaoI;

    @Override
    public DeductionBySettlementSummary get(Long id) throws IOException {
        return deductionBySettlementSummaryDaoI.get(id);
    }

    @Override
    public DeductionBySettlementSummary save(DeductionBySettlementSummary log) throws IOException {
        return deductionBySettlementSummaryDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        deductionBySettlementSummaryDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        deductionBySettlementSummaryDaoI.delete(ids);
    }

    @Override
    public Page<DeductionBySettlementSummary> query() {
        return deductionBySettlementSummaryDaoI.query();
    }

    @Override
    public Page<DeductionBySettlementSummary> query(Specification<DeductionBySettlementSummary> spec) {
        return deductionBySettlementSummaryDaoI.query(spec);
    }

    @Override
    public Page<DeductionBySettlementSummary> query(Pageable pageable) {
        return deductionBySettlementSummaryDaoI.query(pageable);
    }

    @Override
    public Page<DeductionBySettlementSummary> query(Specification<DeductionBySettlementSummary> spec, Pageable pageable) {
        return deductionBySettlementSummaryDaoI.query(spec, pageable);
    }

    @Override
    public List<DeductionBySettlementSummary> getAll() {
        return deductionBySettlementSummaryDaoI.getAll();
    }

    @Override
    public DeductionBySettlementSummary getAllByProjectIdAndCarIdAndReportDate(Long projectId, Long carId, Date reportDate) {
        return deductionBySettlementSummaryDaoI.getAllByProjectIdAndCarIdAndReportDate(projectId, carId, reportDate);
    }

    @Override
    public List<DeductionBySettlementSummary> getAllByProjectIdAndReportDate(Long projectId, Date createDate) {
        return deductionBySettlementSummaryDaoI.getAllByProjectIdAndReportDate(projectId, createDate);
    }

    /*@Override
    public DeductionBySettlementSummary getAllBySummaryIdAndProjectId(Long summaryId, Long projectId) {
        return deductionBySettlementSummaryDaoI.getAllBySummaryIdAndProjectId(summaryId, projectId);
    }*/
}
