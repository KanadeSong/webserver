package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectSettlementTotal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/2 0002 17:47
 */
public interface ProjectSettlementTotalRepository extends JpaRepository<ProjectSettlementTotal, Long>, JpaSpecificationExecutor<ProjectSettlementTotal> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_settlement_total where project_id = ?1 and datediff(report_date, ?2) = 0 and car_id = ?3")
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate, Long carId);

    @Query(nativeQuery = true, value = "select * from project_settlement_total where project_id = ?1 and car_id = ?2 and datediff(report_date,?3) = 0")
    List<ProjectSettlementTotal> getByProjectIdAndCarIdAndReportDate(Long projectId, Long carId, Date reportDate);
}
