package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.DeductionDiggingByMonth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/10 0010 13:42
 */
public interface DeductionDiggingByMonthRepository extends JpaRepository<DeductionDiggingByMonth,Long>, JpaSpecificationExecutor<DeductionDiggingByMonth> {

    @Query(nativeQuery = true, value = "select * from deduction_digging_by_month where project_id = ?1 and datediff(report_date, ?2) = 0")
    List<DeductionDiggingByMonth> getAllByProjectIdAndReportDate(Long projectId, Date reportDate);
}
