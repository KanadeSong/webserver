package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.DeductionBySettlementSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/20 0020 10:07
 */
public interface DeductionBySettlementSummaryRepository extends JpaRepository<DeductionBySettlementSummary,Long>, JpaSpecificationExecutor<DeductionBySettlementSummary> {

    //DeductionBySettlementSummary getAllBySummaryIdAndProjectId(Long summaryId, Long projectId);
    @Query(nativeQuery = true, value = "select * from deduction_by_settlement_summary where project_id = ?1 and car_id = ?2 and datediff(report_date, ?3) = 0")
    DeductionBySettlementSummary getAllByProjectIdAndCarIdAndReportDate(Long projectId, Long carId, Date reportDate);

    @Query(nativeQuery = true, value = "select * from deduction_by_settlement_summary where project_id = ?1 and datediff(create_date, ?2) = 0")
    List<DeductionBySettlementSummary> getAllByProjectIdAndReportDate(Long projectId, Date createDate);
}
