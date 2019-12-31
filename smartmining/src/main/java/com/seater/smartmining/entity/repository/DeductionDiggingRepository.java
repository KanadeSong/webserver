package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.DeductionDigging;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/8 0008 0:46
 */
public interface DeductionDiggingRepository extends JpaRepository<DeductionDigging,Long>, JpaSpecificationExecutor<DeductionDigging> {

    @Query(nativeQuery = true, value = "select * from deduction_digging where project_id = ?1 and machine_id = ?2 and datediff(report_date, ?3) = 0")
    DeductionDigging getAllByProjectIdAndMachineIdAndReportDate(Long projectId, Long machineId, Date reportDate);

    @Query(nativeQuery = true, value = "select * from deduction_digging where project_id = ?1 and datediff(report_date, ?2) = 0")
    List<DeductionDigging> getAllByProjectIdAndReportDate(Long projectId, Date reportDate);

    @Query(nativeQuery = true, value = "select * from deduction_digging where project_id = ?1 and report_date >= ?2 and report_date <=?3")
    List<DeductionDigging> getAllByProjectIdAndTime(Long projectId, Date startTime, Date endTime);
}
