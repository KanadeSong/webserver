package com.seater.smartmining.dao;

import com.seater.smartmining.entity.DeductionBySettlementSummary;
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
 * @Date 2019/5/20 0020 10:14
 */
public interface DeductionBySettlementSummaryDaoI {

    DeductionBySettlementSummary get(Long id) throws IOException;
    DeductionBySettlementSummary save(DeductionBySettlementSummary log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<DeductionBySettlementSummary> query();
    Page<DeductionBySettlementSummary> query(Specification<DeductionBySettlementSummary> spec);
    Page<DeductionBySettlementSummary> query(Pageable pageable);
    Page<DeductionBySettlementSummary> query(Specification<DeductionBySettlementSummary> spec, Pageable pageable);
    List<DeductionBySettlementSummary> getAll();
    //DeductionBySettlementSummary getAllBySummaryIdAndProjectId(Long summaryId, Long projectId);
    DeductionBySettlementSummary getAllByProjectIdAndCarIdAndReportDate(Long projectId, Long carId, Date reportDate);
    List<DeductionBySettlementSummary> getAllByProjectIdAndReportDate(Long projectId, Date createDate);
}
