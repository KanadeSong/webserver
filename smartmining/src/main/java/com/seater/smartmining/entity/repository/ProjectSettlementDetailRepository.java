package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectSettlementDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/2 0002 14:11
 */
public interface ProjectSettlementDetailRepository extends JpaRepository<ProjectSettlementDetail, Long>, JpaSpecificationExecutor<ProjectSettlementDetail> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_settlement_detail where project_id = ?1 and datediff(create_date, ?2) = 0 and car_id = ?3")
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate, Long carId);

    @Query(nativeQuery = true, value = "select * from project_settlement_detail where project_id = ?1 and total_id = ?2 and datediff(report_date, ?3) = 0")
    List<ProjectSettlementDetail> getByProjectIdAndTotalId(Long projectId, Long totalId, Date reportDate);

    @Query(nativeQuery = true, value = "select report_date from project_settlement_detail " +
            " where project_id = ?1 and car_id = ?2 and total_id = ?3 group by report_date")
    List<Map> getReportDateByProjectIdAndCarIdAndTotalId(Long projectId, Long carId, Long totalId);

    @Query(nativeQuery = true, value = "select distance,sum(cars_count) as carsCount,sum(cubic_count) as cubic,sum(amount) as amount from project_settlement_detail" +
            " where total_id = ?1 group by distance")
    List<Map> getTotalInfoByTotalId(Long totalId);
}
