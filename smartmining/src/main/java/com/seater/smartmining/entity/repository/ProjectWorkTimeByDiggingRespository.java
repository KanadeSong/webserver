package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectWorkTimeByDigging;
import com.seater.smartmining.enums.PricingTypeEnums;
import com.seater.smartmining.enums.WorkInfoStatusEnums;
import com.seater.smartmining.enums.WorkStatusEnums;
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
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/1/26 0026 13:46
 */
public interface ProjectWorkTimeByDiggingRespository extends JpaRepository<ProjectWorkTimeByDigging, Long>, JpaSpecificationExecutor<ProjectWorkTimeByDigging> {
    List<ProjectWorkTimeByDigging> getByMaterialIdAndStartTime(Long materialId, Date startTime);

    List<ProjectWorkTimeByDigging> getByProjectIdAndMaterialIdOrderById(Long projectId, Long materialId);

    @Query(nativeQuery = true, value = "select * from project_work_time_by_digging where project_id = ?1" +
            " and material_id = ?2 and start_time >= ?3 and end_time <= ?4 ")
    List<ProjectWorkTimeByDigging> getByProjectIdAndMaterialIdAndTime(Long projectId, Long materialId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select * from project_work_time_by_digging where project_id = ?1 " +
            " and start_time >= ?2 and end_time <= ?3 ")
    List<ProjectWorkTimeByDigging> getByProjectIdAndTime(Long projectId, Date startTime, Date endTime);

    @Transactional
    @Modifying
    @Query("update ProjectWorkTimeByDigging set workStatus = 2, workInfoStatusEnums = 4,endTime = ?2 where id = ?1")
    void setEndTimeById(Long id, Date endTime);

    @Query(nativeQuery = true, value = "select * from project_work_time_by_digging where project_id = ?1" +
            " and material_id = ?2 and start_time is not null and end_time is null ")
    List<ProjectWorkTimeByDigging> getByProjectIdAndMaterialIdByQuery(Long projectId, Long materialId);

    @Query(nativeQuery = true, value = "select * from project_work_time_by_digging where project_id = ?1" +
            " and material_id = ?2 and start_time is null and end_time is null ")
    List<ProjectWorkTimeByDigging> getByProjectIdAndMaterialId(Long projectId, Long materialId);

    @Query(nativeQuery = true, value = "select * from project_work_time_by_digging where project_id = ?1" +
            " and material_id = ?2 and end_time is null ")
    List<ProjectWorkTimeByDigging> getByProjectIdAndMaterialIdAdd(Long projectId, Long materialId);

    @Query(nativeQuery = true, value = "select * from project_work_time_by_digging where project_id = ?1" +
            " and end_time is null and datediff(date_identification, ?2) = 0 ")
    List<ProjectWorkTimeByDigging> getByProjectIdByQuery(Long projectId, Date reportDate);

    @Query(nativeQuery = true, value = "select * from project_work_time_by_digging where project_id = ?1" +
            " and end_time is null and datediff(date_identification, ?2) = 0 and shift = ?3")
    List<ProjectWorkTimeByDigging> getByProjectIdByQueryAndShift(Long projectId, Date reportDate, Integer shift);

    @Query(nativeQuery = true, value = "select date_format(start_time,'%Y-%m-%d') as workday from project_work_time_by_digging " +
            " where project_id = ?1 and material_id = ?2 and status = 2 and start_time >= ?3 and start_time <= ?4 group by date_format(start_time,'%Y-%m-%d')")
    List<Map> getWorkTimeByMaterialIdAndTime(Long projectId, Long materialId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select * from project_work_time_by_digging where project_id = ?1 and material_id = ?2" +
            " and status = 2 and datediff(start_time,?3) = 0")
    List<ProjectWorkTimeByDigging> getTimeByMaterialIdAndCreateTime(Long projectId, Long materialId, Date createTime);

    @Query(nativeQuery = true, value = "select * from project_work_time_by_digging where project_id = ?1 and material_id = ?2" +
            " and status = 2 and create_time >= ?3 and create_time <= ?4")
    List<ProjectWorkTimeByDigging> getTimeByMaterialIdAndCreateTime(Long projectId, Long materialId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select distinct material_code from project_work_time_by_digging where project_id = ?1 and datediff(start_time, ?2) = 0 and shift = ?3")
    List<Map> getAllByProjectIdAndStartTimeAndShift(Long projectId, Date startTime, Integer shift);

    @Query(nativeQuery = true, value = "select distinct material_id from project_work_time_by_digging where project_id = ?1 and start_time >= ?2 and end_time <= ?3")
    List<Map> getMaterialIdByProjectIdAndStartTime(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select distinct material_id, id from project_work_time_by_digging where project_id = ?1 and datediff(start_time, ?2) = 0 order by id desc limit ?3,?4")
    List<Map> getMaterialIdByProjectIdAndStartTimeAndPage(Long projectId, Date dateTime, int cur, int pageSize);

    @Query(nativeQuery = true, value = "select distinct material_id from project_work_time_by_digging where project_id = ?1 and datediff(start_time, ?2) = 0")
    List<Map> getMaterialIdByProjectIdAndStartTime(Long projectId, Date dateTime);

    @Query(nativeQuery = true, value = "select sum(work_time) as workTime, material_id, shift, pricing_type_enums from project_work_time_by_digging" +
            " where project_id = ?1 and datediff(start_time, ?2) = 0 and status = 2 group by material_id, shift, pricing_type_enums")
    List<Map> getWorkTimeByProjectIdAndStartTime(Long projectId, Date reportDate);

    @Query(nativeQuery = true, value = "select sum(work_time) as workTime from project_work_time_by_digging where project_id = ?1 and start_time >= ?2 and start_time <= ?3 and shift = ?4" +
            " and pricing_type_enums = ?5 and material_id = ?6 group by material_id")
    Map getAllByProjectIdAndStartTimeAndShiftAndPricingTypeEnums(Long projectId, Date startTime, Date endTime,Integer shift, Integer pricingType, Long machineId);

    @Query(nativeQuery = true, value = "select * from project_work_time_by_digging where project_id = ?1 and material_id = ?2" +
            " and datediff(start_time,?3) = 0")
    List<ProjectWorkTimeByDigging> getTimeByMachineIdAndCreateTime(Long projectId, Long machineId, Date createTime);

    @Query(nativeQuery = true, value = "select sum(work_time) as workTime from project_work_time_by_digging where project_id = ?1 " +
            " and datediff(start_time, ?2) = 0 and shift = ?3")
    Map getTotalWorkTimeByProjectIdAndDateTimeAndShift(Long projectId, Date dateTime, Integer shift);

    @Query(nativeQuery = true, value = "select * from project_work_time_by_digging where project_id = ?1" +
            " and datediff(start_time, ?2) = 0 and shift = ?3 and status = ?4")
    List<ProjectWorkTimeByDigging> getAllByProjectIdAndStartTimeAndShiftAndWorkStatus(Long projectId, Date dateTime, Integer shift, Integer workStatus);

    @Transactional
    @Modifying
    @Query("update ProjectWorkTimeByDigging set workInfoStatusEnums = ?2 where id = ?1")
    void setWorkInfoStatusEnumsById(Long id, WorkInfoStatusEnums workInfoStatusEnums);

    List<ProjectWorkTimeByDigging> getAllByProjectIdAndMaterialCode(Long projectId, String machineCode);

    @Transactional
    @Modifying
    @Query("update ProjectWorkTimeByDigging set workInfoStatusEnums = ?2, workStatus = ?3 where id = ?1")
    void setWorkInfoStatusEnumsAnsStatusById(Long id, WorkInfoStatusEnums workInfoStatusEnums, WorkStatusEnums workStatus);

    @Query(nativeQuery = true, value = "select distinct material_id from project_work_time_by_digging where project_id = ?1" +
            " and status = 2 and start_time >= ?2 and start_time <= ?3")
    List<Map> getTimeByMaterialIdAndStartTime(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select distinct material_id from project_work_time_by_digging where project_id = ?1" +
            " and status = 2 and start_time >= ?2 and start_time <= ?3 order by material_id")
    List<Map> getAllByProjectIdAndTime(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select material_id, sum(work_time) AS workTime,shift,pricing_type_enums from project_work_time_by_digging where project_id = ?1" +
            " and status = 2 and datediff(start_time, ?2) = 0 group by material_id,shift,pricing_type_enums order by material_id")
    List<Map> getInfoByMaterialIdAndMaterialIdAndStartTime(Long projectId, Date dateTime);

    List<ProjectWorkTimeByDigging> getAllByProjectId(Long projectId);

    @Query(nativeQuery = true, value = "select sum(work_time) as workTime, material_code, shift from project_work_time_by_digging " +
            " where project_id = ?1 and datediff(date_identification,?2) = 0 and start_time is not null group by material_code, shift")
    List<Map> getTotalTimeByProjectIdAndDate(Long projectId, Date date);

    @Query(nativeQuery = true, value = "select sum(work_time) as workTime, pricing_type_enums,material_code  from project_work_time_by_digging" +
            " where project_id = ?1 and create_time <= ?2 group by pricing_type_enums, material_code")
    List<Map> getTotalTimeAndPricingTypeByProjectIdAndDate(Long projectId, Date date);

    @Query(nativeQuery = true, value = "select date_format(start_time,'%Y-%m-%d') as workday from project_work_time_by_digging " +
            "where project_id = ?1 and material_id = ?2 and status = 2 and start_time >= ?3 and start_time <= ?4 group by date_format(start_time,'%Y-%m-%d')")
    List<Map> getAllByProjectIdAndStartTimeAndEndTimeAndMaterialId(Long projectId, Long machineId, Date startDate, Date endDate);

    @Query(nativeQuery = true, value = "select sum(work_time) / 3600 as workTime, date_format(start_time,'%Y-%m-%d') as `time`, pricing_type_enums from project_work_time_by_digging where project_id = ?1" +
            " and start_time >= ?2 and start_time <= ?3 group by date_format(start_time,'%Y-%m-%d'),pricing_type_enums")
    List<Map> getDiggingTimeReport(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select sum(work_time) / 3600 as workTime, date_format(start_time,'%Y-%m') as `time`, pricing_type_enums from project_work_time_by_digging where project_id = ?1" +
            " and start_time >= ?2 and start_time <= ?3 group by date_format(start_time,'%Y-%m'),pricing_type_enums")
    List<Map> getDiggingTimeReportByMonth(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select sum(work_time) / 3600 as workTime, date_format(start_time,'%Y-%m') as `time`, pricing_type_enums from project_work_time_by_digging where project_id = ?1" +
            " and start_time <= ?2 group by date_format(start_time,'%Y-%m'),pricing_type_enums")
    List<Map> getDiggingTimeReportByHistory(Long projectId, Date endTime);

    @Query(nativeQuery = true, value = "select material_code , date_format(start_time,'%Y-%m-%d') as `time`, pricing_type_enums from project_work_time_by_digging" +
            " where project_id = ?1 and start_time >= ?2 and start_time <= ?3 group by date_format(start_time,'%Y-%m-%d'),pricing_type_enums, material_code")
    List<Map> getDiggingTimeInfo(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select material_code, date_format(start_time,'%Y-%m') as `time`, pricing_type_enums from project_work_time_by_digging where project_id = ?1" +
            " and start_time >= ?2 and start_time <= ?3 group by date_format(start_time,'%Y-%m'),pricing_type_enums, material_code")
    List<Map> getDiggingTimeInfoMonth(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select material_code, date_format(start_time,'%Y-%m') as `time`, pricing_type_enums from project_work_time_by_digging where project_id = ?1" +
            " and start_time <= ?2 group by date_format(start_time,'%Y-%m'),pricing_type_enums, material_code")
    List<Map> getDiggingTimeInfoHistory(Long projectId, Date endTime);

    @Query(nativeQuery = true, value = "select sum(work_time) as workTime, pricing_type_enums, shift, material_id from project_work_time_by_digging" +
            " where project_id = ?1 and start_time >= ?2 and start_time <= ?3 group by pricing_type_enums, shift, material_id")
    List<Map> getDiggingInfoByProjectId(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select sum(work_time) / 3600 as workTime, date_format(start_time,'%Y-%m-%d') as `time` from project_work_time_by_digging where project_id = ?1" +
            " and start_time >= ?2 and start_time <= ?3 group by date_format(start_time,'%Y-%m-%d')")
    List<Map> getTotalDiggingTimeReport(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select sum(work_time) / 3600 as workTime, date_format(start_time,'%Y-%m') as `time` from project_work_time_by_digging where project_id = ?1" +
            " and start_time >= ?2 and start_time <= ?3 group by date_format(start_time,'%Y-%m')")
    List<Map> getTotalDiggingTimeReportByMonth(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select sum(work_time) / 3600 as workTime, date_format(start_time,'%Y-%m') as `time` from project_work_time_by_digging where project_id = ?1" +
            " and start_time <= ?2 group by date_format(start_time,'%Y-%m')")
    List<Map> getTotalDiggingTimeReportByHistory(Long projectId, Date endTime);

    @Query(nativeQuery = true, value = "select sum(work_time) / 3600 as workTime, date_format(date_identification,'%Y-%m-%d') as `time` from project_work_time_by_digging where project_id = ?1" +
            " and date_identification >= ?2 and date_identification <= ?3 and pricing_type_enums = ?4 group by date_format(date_identification,'%Y-%m-%d')")
    List<Map> getWorkTimeByPricingType(Long projectId, Date startTime, Date endTime, Integer pricingType);

    @Query(nativeQuery = true, value = "select sum(work_time) / 3600 as workTime, date_format(date_identification,'%Y-%m') as `time` from project_work_time_by_digging where project_id = ?1" +
            " and date_identification >= ?2 and date_identification <= ?3 and pricing_type_enums = ?4 group by date_format(date_identification,'%Y-%m')")
    List<Map> getWorkTimeByPricingTypeMonth(Long projectId, Date startTime, Date endTime, Integer pricingType);

    @Query(nativeQuery = true, value = "select sum(work_time) / 3600 as workTime, date_format(date_identification,'%Y-%m') as `time` from project_work_time_by_digging where project_id = ?1" +
            " and date_identification <= ?2 and pricing_type_enums = ?3 group by date_format(date_identification,'%Y-%m')")
    List<Map> getWorkTimeByPricingTypeHistory(Long projectId, Date endTime, Integer pricingType);

    @Query(nativeQuery = true, value = "select sum(work_time) as workTime, shift, material_id, material_code, place_id, place_name from project_work_time_by_digging" +
            " where project_id = ?1 and datediff(date_identification, ?2) = 0 group by shift, material_id, material_code, place_id, place_name")
    List<Map> reportDiggingWorkTimeByPlace(Long projectId, Date date);

    @Query(nativeQuery = true, value = "select sum(work_time) as workTime, shift, material_id, material_code, data_id, data_name from project_work_time_by_digging" +
            " where project_id = ?1 and datediff(date_identification, ?2) = 0 group by shift, material_id, material_code, data_id, data_name")
    List<Map> reportDiggingWorkTimeByMaterial(Long projectId, Date date);

    @Query(nativeQuery = true, value = "select distinct material_code from project_work_time_by_digging" +
            " where project_id = ?1 and datediff(date_identification, ?2) = 0")
    List<Map> getAttendanceByTime(Long projectId, Date date);

    @Query(nativeQuery = true, value = "select distinct material_code from project_work_time_by_digging" +
            " where project_id = ?1 and date_identification >= ?2 and date_identification <= ?3")
    List<Map> getAttendanceByTimeMonth(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select sum(work_time) as workTime from project_work_time_by_digging" +
            " where project_id = ?1 and material_code = ?2 and datediff(date_identification, ?3) = 0 and shift = ?4 and status = 2")
    Long getAllByProjectIdAndMaterialCodeAndDateIdentificationAndShift(Long projectId, String machineCode, Date date, Integer shift);
}
