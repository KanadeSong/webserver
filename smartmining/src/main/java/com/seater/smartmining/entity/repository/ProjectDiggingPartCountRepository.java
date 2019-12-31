package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectDiggingPartCount;
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
 * @Date 2019/2/28 0028 16:01
 */
public interface ProjectDiggingPartCountRepository extends JpaRepository<ProjectDiggingPartCount, Long>, JpaSpecificationExecutor<ProjectDiggingPartCount> {

    List<ProjectDiggingPartCount> getByProjectIdAndTotalIdAndMachineId(Long projectId, Long totalId, Long machineId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_digging_part_count where project_id = ?1 and datediff(report_date, ?2) = 0 and machine_id = ?3")
    void deleteByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machineId);

    @Query(nativeQuery = true, value = "select distinct machine_id from project_digging_part_count where project_id = ?1 " +
            " and create_date >= ?2 and create_date <= ?3")
    List<Map> getMachineIdByProjectIdAndTime(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select sum(work_time_by_day_by_timer) as workTimeByDayByTimer, sum(work_time_by_night_by_time) as workTimeByNightByTime,sum(work_time_by_day_by_cubic) as workTimeByDayByCubic," +
            " sum(work_time_by_night_by_cubic) as workTimeByNightByCubic,sum(work_time_by_timer) as workTimeByTimer, sum(work_time_by_cubic) as workTimeByCubic,price_by_timer,sum(amount_by_timer) as amountByTimer," +
            " sum(amount_by_cubic) as amountByCubic,sum(amount_by_count) as amountByCount,sum(total_cubic_by_timer) as totalCubicByTimer,sum(total_cubic_by_cubic) as totalCubicByCubic," +
            " sum(total_count_by_timer) as totalCountByTimer,sum(total_count_by_cubic) as totalCountByCubic,sum(oil_count) as oilCount,sum(amount_by_oil) as amountByOil from project_digging_part_count" +
            " where project_id = ?1 and machine_id = ?2 and datediff(create_date, ?3) = 0" +
            " group by price_by_timer")
    List<Map> getByProjectIdAndMachineIdAndTime(Long projectId, Long machineId, Date time);
}
