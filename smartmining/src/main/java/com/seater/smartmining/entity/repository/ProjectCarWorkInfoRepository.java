package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectCarWorkInfo;
import com.seater.smartmining.entity.Score;
import com.seater.smartmining.entity.Shift;
import com.seater.smartmining.enums.PricingTypeEnums;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ProjectCarWorkInfoRepository extends JpaRepository<ProjectCarWorkInfo, Long>, JpaSpecificationExecutor<ProjectCarWorkInfo> {
    ProjectCarWorkInfo getByProjectIdAndCarIdAndTimeCheck(Long projectId, Long carId, Date timeCheck);

    ProjectCarWorkInfo getByProjectIdAndDiggingMachineIdAndTimeCheck(Long projectId, Long diggingMachineId, Date timeCheck);

    @Query(nativeQuery = true, value = "select * from project_car_work_info where project_id = ?1 and car_id = ?2 and time_load = ?3")
    ProjectCarWorkInfo getByProjectIdAndCarIdAndTimeLoad(Long projectId, Long carId, Date timeLoad);

    ProjectCarWorkInfo getByProjectIdAndCarIdAndTimeDischarge(Long projectId, Long carId, Date timeDischarge);

    @Query(nativeQuery = true, value = "select payable_distance, count(*) as totalCount, sum(cubic) as totalCubic, sum(amount) as totalAmount " +
            "from project_car_work_info where pass = 1 and status = 7 and is_vaild != 1 and is_vaild != 3 and info_valid = true and project_id = ?1 and date_identification >= ?2 and date_identification <= ?3 group by " +
            "payable_distance order by payable_distance")
    List<Map> getDistanceListByProjectIdAndTime(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select car_id, car_owner_id, car_owner_name, car_code, shift, payable_distance as payable_distance, count(*) as count, " +
            "sum(amount) as amount, sum(cubic) as cubic from project_car_work_info where pass = 1 and status = 7 and is_vaild != 1 and is_vaild != 3 and info_valid = true and project_id = ?1 and " +
            "date_identification >= ?2 and date_identification <= ?3 group by car_id, car_owner_id, car_owner_name, car_code, payable_distance, shift order by car_id")
    List<Map> getCountListByProjectIdAndTime(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select car_id, count(*) as count, sum(amount) as amount, sum(cubic) as cubic, sum(payable_distance) as" +
            " mileage from project_car_work_info where pass = 1 and status = 7 and is_vaild != 1 and is_vaild != 3 and info_valid = true and project_id = ?1 and date_identification >= ?2 and date_identification <= ?3" +
            " group by car_id, car_code order by car_id")
    List<Map> getCarGrandTotalListByProjectIdAndTime(Long projectId, Date startTime, Date endTime);


    @Query(nativeQuery = true, value = "select count(*) as count, sum(cubic) as cubic, pricing_type " +
            " from project_car_work_info where pass = 1 and status = 7 and is_vaild != 2 and is_vaild != 3 and info_valid = true and project_id = ?1 and time_discharge >= ?2 and time_discharge <= ?3 and digging_machine_id = ?4" +
            " group by pricing_type")
    List<Map> getDiggingDayCountListByProjectIdAndTimeAndMachineId(Long projectId, Date startTime, Date endTime, Long machineId);

    /*@Query(nativeQuery = true, value = "select digging_machine_id,count(*) as count, sum(cubic) as cubic, digging_machine_code,pricing_type " +
            " from project_car_work_info where pass = 1 and status = 7 and is_vaild != 2 and is_vaild != 3  and project_id = ?1 and date_identification >= ?2 and date_identification <= ?3" +
            " group by digging_machine_id, digging_machine_code,pricing_type order by digging_machine_id")
    List<Map> getDiggingDayCountListByProjectIdAndTime(Long projectId, Date startTime, Date endTime);*/

    @Query(nativeQuery = true, value = "select digging_machine_id, count(*) as count, sum(cubic) as cubic, digging_machine_code " +
            " from project_car_work_info where pass = 1 and status = 7 and is_vaild != 2 and is_vaild != 3 and info_valid = true and project_id = ?1 and date_identification >= ?2 and date_identification <= ?3" +
            " and pricing_type = ?4 group by digging_machine_id, digging_machine_code order by digging_machine_id")
    List<Map> getDiggingDayCountListByProjectIdAndTime(Long projectId, Date startTime, Date endTime, Integer pricingType);

    @Query(nativeQuery = true, value = "select digging_machine_id, count(*) as count, sum(cubic) as cubic, sum(amount) as amount, digging_machine_code, shift" +
            " from project_car_work_info where pass = 1 and status = 7 and is_vaild != 2 and is_vaild != 3 and info_valid = true and project_id = ?1 and date_identification >= ?2 and date_identification <= ?3" +
            " group by digging_machine_id, digging_machine_code, shift order by digging_machine_id")
    List<Map> getDiggingDayCountListByProjectIdAndTimeGroupByShift(Long projectId, Date startTime, Date endTime);

    //计方详情
    @Query(nativeQuery = true, value = "select material_id,material_name, count(*) as count, sum(cubic) as cubic from project_car_work_info" +
            " where pass = 1 and status = 7 and is_vaild != 2 and pricing_type = 2 and is_vaild != 3 and info_valid = true and project_id = ?1 and digging_machine_id = ?2 and time_discharge >= ?3 and time_discharge <= ?4 group by " +
            " material_id,material_name order by material_id")
    List<Map> getMaterialDetailByProjectIdAndMachineIdAndTime(Long projectId, Long machineId, Date startTime, Date endTime);

    //计方详情 改动之前的代码
    @Query(nativeQuery = true, value = "select material_id,digging_machine_id, count(*) as count, sum(cubic) as cubic from project_car_work_info" +
            " where pass = 1 and status = 7 and is_vaild != 2 and pricing_type = 2 and is_vaild != 3 and info_valid = true and project_id = ?1 and date_identification >= ?2 and date_identification <= ?3 group by " +
            " material_id,digging_machine_id order by material_id")
    List<Map> getMaterialDetailByProjectIdAndMachineIdAndTime(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select sum(cubic) as cubic,car_id,car_code from project_car_work_info " +
            " where pass = 1 and status = 7 and is_vaild != 1 and is_vaild != 3 and info_valid = true and datediff(report_date, ?1) = 0 group by car_id,car_code")
    List<Map> getProjectCarWorkInfoByCreateDate(Date reportDate);

    @Query(nativeQuery = true, value = "select payable_distance,count(*) as count,sum(cubic) as cubic,sum(amount) as amount,report_date from project_car_work_info" +
            " where project_id = ?1 and report_date >= ?2 and report_date <= ?3 and car_id = ?4" +
            " group by car_code,payable_distance,report_date order by report_date")
    List<Map> getSettlementDetailByProjectIdAndTimeAndCarId(Long projectId, Date startTime, Date endTime, Long carId);

    @Query(nativeQuery = true, value = "select car_id,car_code,count(*) as count,sum(cubic) as cubic,date_identification,material_id " +
            " from project_car_work_info where pass = 1 and status = 7 and is_vaild != 2 and is_vaild != 3 and info_valid = true and project_id = ?1 and digging_machine_id = ?2" +
            " and date_identification in (?3) and pricing_type = ?4 group by car_id,car_code,date_identification,material_id" +
            " order by car_id")
    List<Map> getCubicDetailByProjectIdAndDiggingMachineIdAndTime(Long projectId, Long machineId, List<String> dateList, Integer pricingType);

    @Query(nativeQuery = true, value = "select count(*) as count,sum(cubic) as cubic,date_identification" +
            " from project_car_work_info where pass = 1 and status = 7 and is_vaild != 2 and is_vaild != 3 and info_valid = true and project_id = ?1 and digging_machine_id = ?2" +
            " and date_identification in (?3) and pricing_type = ?4 group by date_identification,material_id")
    List<Map> getElseTotalByProjectIdAndDiggingMachineIdAndTime(Long projectId, Long machineId, List<String> dateList, Integer pricingType);

    @Query(nativeQuery = true, value = "select car_id,car_code,count(*) as count,sum(cubic) as cubic from project_car_work_info" +
            " where pass = 1 and status = 7 and is_vaild != 2 and is_vaild != 3 and info_valid = true and project_id = ?1 and digging_machine_id = ?2 and date_identification >= ?3 and date_identification <= ?4 and pricing_type = ?5" +
            " group by car_id,car_code order by car_id")
    List<Map> getDetailTotalByProjectIdAndMachineIdAndTime(Long projectId, Long machineId, Date startTime, Date endTime, Integer pricingType);

    @Query(nativeQuery = true, value = "select distance,material_id,material_name,count(*) as count,sum(cubic) as cubic,sum(amount) as amount,DATE_FORMAT(time_discharge,'%Y-%m-%d') as time_discharge from project_car_work_info" +
            " where pass = 1 and STATUS = 7 and is_vaild != 1 and is_vaild != 3 and info_valid = true and project_id = ?1 and car_id = ?2 and time_discharge >= ?3 and time_discharge <= ?4" +
            " group by distance,material_id,material_name,DATE_FORMAT(time_discharge,'%Y-%m-%d')")
    List<Map> getByCubicDetailOrderByProjectIdAndCarIdAndTime(Long projectId, Long carId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select distinct digging_machine_id from project_car_work_info" +
            " where pass = 1 and status = 7 and is_vaild != 2 and is_vaild != 3 and info_valid = true and project_id = ?1 and datediff(time_discharge, ?4) = 0 limit ?2, ?3")
    List<Map> getMachineIdByProjectIdAndPageAndTime(Long projectId, Integer current, Integer pageSize, Date time);

    @Query(nativeQuery = true, value = "select count(*) as `count`,sum(payable_distance) as mileage,material_id,material_name,sum(cubic) as cubic,sum(amount) as amount,distance,DATE_FORMAT(time_discharge,'%Y-%m-%d') as reportDate" +
            " from project_car_work_info where pass = 1 and status = 7 and is_vaild != 1 and is_vaild != 3 and info_valid = true and project_id = ?1 and car_id = ?2 and time_discharge >= ?3 and time_discharge <= ?4" +
            " group by car_id,date_format(time_discharge,'%Y-%m-%d'),material_id,material_name,distance")
    List<Map> getAllSettlementByProjectIdAndCarIdAndTime(Long projectId, Long carId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select count(*) as `count`,sum(amount) as amount,sum(payable_distance) as mileage,date_format(time_discharge,'%Y-%m-%d') AS reportDate from project_car_work_info where pass = 1 and status = 7" +
            " and is_vaild != 1 and is_vaild != 3 and info_valid = true and project_id = ?1 and car_id = ?2 and time_discharge >= ?3 and time_discharge <= ?4 group by DATE_FORMAT(time_discharge,'%Y-%m-%d')")
    List<Map> getCarsCountByProjectIdAndCarIdAndTime(Long projectId, Long carId, Date beginDate, Date endDate);

    @Query(nativeQuery = true, value = "select sum(cubic) as cubic,material_id, pricing_type,count(*) as count from project_car_work_info where pass = 1 and status = 7 and is_vaild != 2 and is_vaild != 3" +
            " and project_id = ?1 and digging_machine_id = ?2 and datediff(date_identification, ?3) =0" +
            " group by material_id,pricing_type")
    List<Map> getSumCubicByTime(Long projectId, Long machineId, Date date);

    @Query(nativeQuery = true, value = "select distinct date_identification from project_car_work_info where pass = 1 and status = 7 and is_vaild != 2 and is_vaild != 3" +
            " and project_id = ?1 and digging_machine_id =?2 and date_identification >= ?3 and date_identification <= ?4")
    List<Map> getDateIdentificationByMachineId(Long projectId, Long machineId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select digging_machine_id,digging_machine_code,sum(cubic) as cubic,count(*) as count from project_car_work_info where pass = 1 and status = 7 and is_vaild != 2 and is_vaild != 3" +
            " and project_id = ?1 and datediff(date_identification, ?2) =0 group by digging_machine_id,digging_machine_code")
    List<Map> getMachineIdListByDate(Long projectId, Date time);

    @Query(nativeQuery = true, value = "select distinct car_code from project_car_work_info where project_id = ?1 and shift = ?2 and datediff(date_identification, ?3) =0 group by car_code")
    List<Map> getCountByProjectIdAndShiftAndDate(Long projectId, Integer shift, Date date);

    @Query(nativeQuery = true, value = "select sum(cubic) as cubic, count(*) as count,car_code, shift from project_car_work_info " +
            " where pass = 1 and status = 7 and is_vaild != 2 and is_vaild != 3 and info_valid = true and project_id = ?1 and datediff(date_identification, ?2) =0" +
            " group by car_code, shift")
    List<Map> getAppDiggingInfoByProjectIdAndDate(Long projectId, Date date);

    @Query(nativeQuery = true, value = "select count(*) as count from project_car_work_info" +
            " where project_id = ?1 and datediff(date_identification, ?2) = 0 and pass = ?3 and shift = ?4")
    Map getCarsCountByProjectIdAndDateIdentificationAndPass(Long projectId, Date date, Integer pass, Integer shift);

    @Query(nativeQuery = true, value = "select * from project_car_work_info where project_id = ?1 and datediff(date_identification, ?2) = 0 and pass = ?3 and shift = ?4")
    List<ProjectCarWorkInfo> getCarsCountUnPassByProjectIdAndDate(Long projectId, Date date, Integer pass, Integer shift);

    @Query(nativeQuery = true, value = "select distinct car_id from project_car_work_info" +
            " where status = 7 and is_vaild != 1 and is_vaild != 3 and info_valid = true and project_id = ?1 and datediff(date_identification, ?2) = 0 and shift = ?3")
    List<Map> getCarsCountByProjectIdAndDateIdentification(Long projectId, Date date, Integer shift);

    //根据当前日期 获取历史信息
    @Query(nativeQuery = true, value = "select sum(cubic) as cubic, sum(amount) as amount, count(*) as count, sum(payable_distance) as distance from project_car_work_info" +
            " where pass = 1 and status = 7 and is_vaild != 1 and is_vaild != 3 and info_valid = true and project_id = ?1 and date_identification <= ?2")
    Map getHistoryInfoByTime(Long projectId, Date startTime);

    @Query(nativeQuery = true, value = "select count(*) as count, car_code, shift from project_car_work_info" +
            " where project_id = ?1 and datediff(date_identification, ?2) = 0 and pass = false" +
            " group by car_code, shift")
    List<Map> getUnpassInfoByProjectIdAndDate(Long projectId, Date date);

    @Query(nativeQuery = true, value = "select distinct date_identification from project_car_work_info where project_id = ?1 and date_identification <= ?2")
    List<Map> countByProjectIdAndDateIdentification(Long projectId, Date date);

    @Query(nativeQuery = true, value = "select count(*) from project_car_work_info where project_id = ?1 and date_identification <= ?2 and material_id = ?3" +
            " and pass = 1 and status = 7 and is_vaild != 1 and is_vaild != 3 and info_valid = true")
    Integer countByProjectIdAndDateIdentificationAndMaterialId(Long projectId, Date date, Long materialId);

    @Query(nativeQuery = true, value = "select sum(cubic) as cubic,material_id, pricing_type,digging_machine_code,count(*) as count from project_car_work_info where pass = 1 and status = 7 and is_vaild != 2 and is_vaild != 3" +
            " and project_id = ?1 and date_identification <= ?2 group by material_id,pricing_type,digging_machine_code")
    List<Map> getTotalCubicAndCountByProjectIdAndDateIdentification(Long projectId, Date date);

    @Query(nativeQuery = true, value = "select sum(cubic) as cubic, count(*) as count, material_id,material_name from project_car_work_info " +
            " where project_id = ?1 and pass = 1 and status = 7 and info_valid = true and is_vaild != 1 and is_vaild != 3 group by material_id,material_name")
    List<Map> getByProjectId(Long projectId);

    @Query(nativeQuery = true, value = "select sum(cubic) as cubic, count(*) as count, material_id,material_name from project_car_work_info" +
            " where project_id = ?1 and pass = 1 and status = 7 and info_valid = true and is_vaild != 1 and is_vaild != 3" +
            " and date_identification >= ?2 and date_identification <= ?3 group by material_id,material_name")
    List<Map> getByProjectIdAndBetweenTime(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select sum(cubic) as cubic, count(*) as count, material_id,material_name from project_car_work_info" +
            " where project_id = ?1 and pass = 1 and status = 7 and info_valid = true and is_vaild != 1 and is_vaild != 3" +
            " and datediff(date_identification, ?2) = 0 group by material_id,material_name")
    List<Map> getByProjectIdAndTime(Long projectId, Date date);

    @Query(nativeQuery = true, value = "select sum(cubic) as cubic, count(*) as count, material_id,material_name from project_car_work_info" +
            " where project_id = ?1 and pass = 1 and status = 7 and info_valid = true and is_vaild != 1 and is_vaild != 3" +
            " and date_identification <= ?2 group by material_id,material_name")
    List<Map> getByProjectIdAndBetweenTimeHistory(Long projectId, Date endTime);

    @Query(nativeQuery = true, value = "select count(*) as `count`,date_identification from project_car_work_info where project_id = ?1" +
            " and date_identification >=?2 and date_identification <=?3 and pass = 1 and status = 7 and info_valid = true and is_vaild != 1 and is_vaild != 3 group by date_identification order by date_identification")
    List<Map> getDiggingWorkReport(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select count(*) as `count`,date_format(date_identification, '%Y-%m') as date_identification from project_car_work_info where project_id = ?1" +
            " and date_identification >=?2 and date_identification <=?3 and pass = 1 and status = 7 and info_valid = true and is_vaild != 1 and is_vaild != 3 group by date_format(date_identification, '%Y-%m')")
    List<Map> getDiggingWorkReportMonth(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select count(*) as `count`,date_format(date_identification, '%Y-%m') as date_identification from project_car_work_info where project_id = ?1" +
            " and date_identification <=?2 and pass = 1 and status = 7 and info_valid = true and is_vaild != 1 and is_vaild != 3 group by date_format(date_identification, '%Y-%m')")
    List<Map> getDiggingWorkReportHistory(Long projectId, Date endTime);

    //不及格数量
    @Query(nativeQuery = true, value = "select count(*) as `count`, date_identification from project_car_work_info where project_id = ?1 and info_valid = true" +
            " and date_identification >=?2 and date_identification <=?3 and (pass = 2 or (is_vaild != 0 and is_vaild != 1)) group by date_identification")
    List<Map> getQualificationReport(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select count(*) as count, date_format(date_identification, '%Y-%m') as date_identification from project_car_work_info where project_id = ?1 and info_valid = true" +
            " and date_identification >=?2 and date_identification <=?3 and (pass = 2 or (is_vaild != 0 and is_vaild != 1)) group by date_format(date_identification, '%Y-%m')")
    List<Map> getQualificationReportMonth(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select count(*) as count, date_format(date_identification, '%Y-%m') as date_identification from project_car_work_info where project_id = ?1 and info_valid = true" +
            " and date_identification <=?2 and (pass = 2 or (is_vaild != 0 and is_vaild != 1)) group by date_format(date_identification, '%Y-%m')")
    List<Map> getQualificationReportHistory(Long projectId, Date endTime);

    @Query(nativeQuery = true, value = "select count(*) as count from project_car_work_info where project_id = ?1 and date_identification >=?2 and date_identification <=?3 and status =?4 and pass = 1 and is_vaild != 1 and is_vaild != 3 and info_valid = true")
    List<Map> findByProjectIdAndStatus(Long projectId, Date startDate, Date endDate, Integer status);

    //获取渣车出勤数量  不论合不合格 不论是否有效 都计入出勤
    @Query(nativeQuery = true, value = "select distinct car_code from project_car_work_info where project_id = ?1" +
            " and datediff(date_identification, ?2) = 0")
    List<Map> getCarAttendanceReport(Long projectId, Date date);

    @Query(nativeQuery = true, value = "select date_identification from project_car_work_info where project_id = ?1 and date_identification >= ?2 and date_identification <= ?3 group by date_identification")
    List<Map> getCarAttendanceDateReport(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select distinct car_code from project_car_work_info where project_id = ?1" +
            " and date_identification >= ?2 and date_identification <= ?3")
    List<Map> getCarAttendanceReportMonth(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select date_format(date_identification, '%Y-%m') as date_identification from project_car_work_info where project_id = ?1" +
            " and date_identification >= ?2 and date_identification <= ?3 group by date_format(date_identification, '%Y-%m')")
    List<Map> getCarAttendanceDateReportMonth(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select count(*) as count, date_identification from project_car_work_info where project_id = ?1" +
            " and date_identification >=?2 and date_identification <=?3 and (pass = 2 or (is_vaild != 0 and is_vaild != 2)) " +
            " and status = 7 and info_valid = true group by date_identification")
    List<Map> getQualificationCarReport(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select count(*) as count, date_format(date_identification, '%Y-%m') as date_identification from project_car_work_info where project_id = ?1" +
            " and date_identification >=?2 and date_identification <=?3 and (pass = 2 or (is_vaild != 0 and is_vaild != 2))" +
            " and status = 7 and info_valid = true group by date_format(date_identification, '%Y-%m')")
    List<Map> getQualificationCarReportMonth(Long projectId, Date startTime, Date endTime);

    //查询指定日期内所有车辆总数
    @Query(nativeQuery = true, value = "select count(*) as `count`, date_identification from project_car_work_info where project_id = ?1" +
            " and date_identification >=?2 and date_identification <=?3 and status = 7 and info_valid = true" +
            " group by date_identification")
    List<Map> getCarsCountByDate(Long projectId, Date startTime, Date endTime);

    //查询指定月份内所有车辆总数
    @Query(nativeQuery = true, value = "select count(*) as `count`, date_format(date_identification, '%Y-%m') as date_identification from project_car_work_info where project_id = ?1" +
            " and date_identification >=?2 and date_identification <=?3 and status = 7 and info_valid = true" +
            " group by date_format(date_identification, '%Y-%m')")
    List<Map> getCarsCountByDateMonth(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select count(*) as `count`, date_format(date_identification, '%Y-%m') as date_identification from project_car_work_info where project_id = ?1" +
            " and date_identification <=?3 and status = 7 and info_valid = true group by date_format(date_identification, '%Y-%m')")
    List<Map> getCarsCountByDateHistory(Long projectId, Date endTime);

    @Query(nativeQuery = true, value = "select count(*) as count, sum(cubic) as cubic, sum(payable_distance) as mileage, date_identification from project_car_work_info" +
            " where project_id = ?1 and date_identification >=?2 and date_identification <=?3 and pass = 1 and is_vaild != 1 and is_vaild != 3 and info_valid = true group by date_identification")
    List<Map> getCarCubicInfo(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select count(*) as count, sum(cubic) as cubic, sum(payable_distance) as mileage, date_format(date_identification, '%Y-%m') as date_identification from project_car_work_info" +
            " where project_id = ?1 and date_identification >=?2 and date_identification <=?3 and pass = 1 and is_vaild != 1 and is_vaild != 3 and info_valid = true" +
            " group by date_format(date_identification, '%Y-%m')")
    List<Map> getCarCubicInfoMonth(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select car_code, count(*) as count from project_car_work_info where project_id = ?1" +
            " and time_discharge >= ?2 and time_discharge <= ?3 and status = 7 and info_valid = true group by car_code")
    List<Map> getFinishCarCountByProjectIdAndTime(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select * from project_car_work_info where project_id = ?1" +
            " and time_discharge >= ?2 and time_discharge <= ?3 and pass = 1 and is_vaild != 1 and is_vaild != 3 and info_valid = true")
    List<ProjectCarWorkInfo> getAllByProjectIdAndTime(Long projectId, Date startTime, Date endTime);


    @Query(nativeQuery = true, value = "select count(*) as `count`,distance,car_id, car_code, slag_site_id, slag_site_name,shift from project_car_work_info" +
            " where project_id = ?1 and time_discharge >= ?2 and time_discharge <= ?3" +
            " and pass = 1 and is_vaild != 1 and is_vaild != 3 and status = 7 and info_valid = true" +
            " group by distance,car_id, car_code, slag_site_id, slag_site_name, shift order by car_code")
    List<Map> getReportInfoGroupBySlagSite(Long projectId, Date startTime, Date endTime);

    //修改前 todo 暂留勿删
    /*@Query(nativeQuery = true, value = "select count(*) as `count`,car_id, car_code, slag_site_id, slag_site_name,shift from project_car_work_info" +
            " where project_id = ?1 and time_discharge >= ?2 and time_discharge <= ?3" +
            " and pass = 1 and is_vaild != 1 and is_vaild != 3 and status = 7" +
            " group by car_id, car_code, slag_site_id, slag_site_name, shift order by car_code")
    List<Map> getReportInfoGroupBySlagSite(Long projectId, Date startTime, Date endTime);*/

    @Query(nativeQuery = true, value = "select count(*) as `count`,date_format(date_identification, '%Y-%m-%d') as date_identification from project_car_work_info where project_id = ?1" +
            " and date_identification >=?2 and date_identification <=?3 and pricing_type = ?4 and pass = 1 and status = 7 and is_vaild != 2 and is_vaild != 3 and info_valid = true" +
            " group by date_format(date_identification, '%Y-%m-%d') order by date_format(date_identification, '%Y-%m-%d')")
    List<Map> getTotalCountByTimer(Long projectId, Date startTime, Date endTime, Integer pricingType);

    @Query(nativeQuery = true, value = "select count(*) as `count`, date_format(date_identification, '%Y-%m') as date_identification from project_car_work_info where project_id = ?1" +
            " and date_identification >=?2 and date_identification <=?3 and pricing_type = ?4 and pass = 1 and status = 7 and is_vaild != 2 and is_vaild != 3 and info_valid = true group by date_format(date_identification, '%Y-%m')")
    List<Map> getTotalCountByTimerMonth(Long projectId, Date startTime, Date endTime, Integer pricingType);

    @Query(nativeQuery = true, value = "select count(*) as `count`, date_format(date_identification, '%Y-%m') as date_identification from project_car_work_info where project_id = ?1" +
            " and date_identification <=?2 and pricing_type = ?3 and pass = 1 and status = 7 and is_vaild != 2 and is_vaild != 3 and info_valid = true group by date_format(date_identification, '%Y-%m')")
    List<Map> getTotalCountByTimerHistory(Long projectId, Date endTime, Integer pricingType);

    @Query(nativeQuery = true, value = "select count(*) as `count`,date_format(date_identification, '%Y-%m-%d') as date_identification from project_car_work_info where project_id = ?1" +
            " and date_identification >=?2 and date_identification <=?3 and pass = 1 and status = 7 and is_vaild != 2 and is_vaild != 3 and info_valid = true" +
            " group by date_format(date_identification, '%Y-%m-%d') order by date_format(date_identification, '%Y-%m-%d')")
    List<Map> getTotalCount(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select count(*) as `count`, date_format(date_identification, '%Y-%m') as date_identification from project_car_work_info where project_id = ?1" +
            " and date_identification >=?2 and date_identification <=?3 and pass = 1 and status = 7 and is_vaild != 2 and is_vaild != 3 and info_valid = true group by date_format(date_identification, '%Y-%m')")
    List<Map> getTotalCountMonth(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select count(*) as `count`, date_format(date_identification, '%Y-%m') as date_identification from project_car_work_info where project_id = ?1" +
            " and date_identification <=?2 and pass = 1 and status = 7 and is_vaild != 2 and is_vaild != 3 and info_valid = true group by date_format(date_identification, '%Y-%m')")
    List<Map> getTotalCountHistory(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select * from project_car_work_info where project_id = ?1" +
            " and car_code = ?2 and datediff(date_identification, ?3) = 0 and time_discharge is null and info_valid = true")
    List<ProjectCarWorkInfo> getAllByProjectIdAndCarCodeAndDateIdentification(Long projectId, String carCode, Date date);

    @Query(nativeQuery = true, value = "select count(*) as `count` from project_car_work_info" +
            " where car_code = ?2 and project_id = ?1 and datediff(date_identification, ?3) = 0 and shift = ?4" +
            " and pass = 1 and status = 7 and is_vaild != 1 and is_vaild != 3 and info_valid = true")
    Long getCarsCountByProjectIdAndDateIdentificationAndCarCode(Long projectId, String carCode, Date date, Integer shift);

    @Query(nativeQuery = true, value = "select * from project_car_work_info where project_id = ?1" +
            " and datediff(date_identification, ?2) = 0 and shift = ?3 and info_valid = true")
    List<ProjectCarWorkInfo> getAllByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift);

    @Query(nativeQuery = true, value = "select car_id, car_owner_id, car_owner_name, car_code, payable_distance as payable_distance, count(*) as count, " +
            "sum(amount) as amount, sum(cubic) as cubic from project_car_work_info where pass = 1 and status = 7 and is_vaild != 1 and is_vaild != 3 and info_valid = true and project_id = ?1 and " +
            "date_identification >= ?2 and date_identification <= ?3 group by car_id, car_owner_id, car_owner_name, car_code, payable_distance order by car_id")
    List<Map> getWorkInfoListByProjectIdAndTime(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select distinct payable_distance from project_car_work_info" +
            " where project_id = ?1 and date_identification >= ?2 and date_identification <= ?3" +
            " and status = 7 and pass = 1 and is_vaild != 1 and is_vaild != 3 and info_valid = true order by payable_distance")
    List<Map> getDistinctByProjectIdAndTime(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select count(*) as count from project_car_work_info" +
            " where project_id = ?1 and date_identification >= ?2 and date_identification <= ?3" +
            " and status = 7 and pass = 1 and is_vaild != 1 and is_vaild != 3 and info_valid = true and material_id = 2")
    Integer getCoalCountByTime(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select distinct car_code from project_car_work_info" +
            " where project_id = ?1 and date_identification >= ?2 and date_identification <= ?3" +
            " and status = 7 and pass = 1 and is_vaild != 1 and is_vaild != 3 and info_valid = true and shift = ?4")
    List<Map> getOnDutyCountByProjectIdAndTime(Long projectId, Date startTime, Date endTime, Integer shift);

    //数据修复使用
    @Query(nativeQuery = true, value = "select * from project_car_work_info where project_id = ?1 and date_identification > ?2 and date_identification < ?3")
    List<ProjectCarWorkInfo> getAllByProjectIdAndDateTime(Long projectId, Date startTime, Date endTime);

    List<ProjectCarWorkInfo> findByProjectIdAndTimeLoadBetween(Long projectId, Date startTime, Date endTime);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_car_work_info where project_id = ?1 and time_discharge >= ?2 and time_discharge <= ?3")
    void deleteAllByProjectIdAndTimeDischarge(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select * from project_car_work_info where project_id = ?1" +
            " and time_load >= ?2 and time_load <= ?3 and status = ?4 and car_code = ?5 order by time_load desc")
    List<ProjectCarWorkInfo> getAllByProjectIdAndTimeLoadHalf(Long projectId, Date startTime, Date endTime, Integer status, String carCode);

    @Query(nativeQuery = true, value = "select * from project_car_work_info where project_id = ?1" +
            " and datediff(date_identification, ?2) = 0 and shift = ?3 and status != ?4")
    List<ProjectCarWorkInfo> getAllByProjectIdAndDateIdentificationAndShiftAndStatusByError(Long projectId, Date date, Integer shift, Integer status);

    @Query(nativeQuery = true, value = "select * from project_car_work_info where time_discharge = " +
            " (select max(time_discharge) from project_car_work_info where project_id = ?1 and car_code = ?2 and status = 7 and pass = 1)" +
            " and project_id = ?1 and car_code = ?2 and status = 7 and pass = 1")
    ProjectCarWorkInfo getAllByProjectIdAndCarCodeAndMaxTimeDischarge(Long projectId, String carCode);

    @Query(nativeQuery = true, value = "select * from project_car_work_info where time_discharge = " +
            " (select max(time_discharge) from project_car_work_info where project_id = ?1 and car_code = ?2 and status = 7 and pass = 1 and time_discharge <= ?3)" +
            " and project_id = ?1 and car_code = ?2 and status = 7 and pass = 1")
    ProjectCarWorkInfo getAllByProjectIdAndCarCodeAndMaxTimeDischarge(Long projectId, String carCode, Date timeDischarge);

    @Query(nativeQuery = true, value = "select count(*) as count, car_code, car_id from project_car_work_info" +
            " where project_id = ?1 and datediff(date_identification, ?2) = 0 and shift = ?3" +
            " and status = ?4 and pass = 1 and info_valid = true" +
            " group by car_code, car_id order by car_code")
    List<Map> getAllByProjectIdAndDateIdentificationAndShiftAndStatus(Long projectId, Date date, Integer shift, Integer status);

    @Query(nativeQuery = true, value = "select count(*) as count, car_code, car_id, merge_code, merge_message from project_car_work_info" +
            " where project_id = ?1 and datediff(date_identification, ?2) = 0 and shift = ?3" +
            " and status = ?4 and pass = 1" +
            " group by car_code, car_id, merge_code, merge_message order by car_code")
    List<Map> getAllByProjectIdAndDateIdentificationAndShiftAndStatusGroupByErrorCode(Long projectId, Date date, Integer shift, Integer status);

    @Query(nativeQuery = true, value = "select distinct merge_code from project_car_work_info" +
            " where project_id = ?1 and datediff(date_identification, ?2) = 0 and shift = ?3" +
            " and status = ?4 and pass = 1 and info_valid = true")
    List<Map> getMergeCodeByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift, Integer status);

    @Query(nativeQuery = true, value = "select count(*) as count,sum(payable_distance) as distance, material_id, material_name from project_car_work_info" +
            " where project_id = ?1 and datediff(date_identification, ?2) = 0 and shift = ?3" +
            " and status = ?4 and car_code = ?5 group by material_id, material_name order by car_code desc")
    List<Map> getTotalCountByProjectIdAndDateIdentificationAndShiftAndStatusAndCarCode(Long projectId, Date date, Integer shift, Integer status, String carCode);
    /**
     * 测试专用
     * @param projectId
     * @param startTime
     * @param endTime
     * @return
     */
    @Query(nativeQuery = true, value = "select * from project_car_work_info where project_id = ?1" +
            " and time_discharge >= ?2 and time_discharge <= ?3 and status = 7 and pass = 1")
    List<ProjectCarWorkInfo> getAllByProjectIdAndTimeDischargeAndStatus(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select * from project_car_work_info where project_id = 1" +
            " and status = 7 and pass = 1 and datediff(date_identification, '2019-12-13 00:00:00') = 0" +
            " and shift = 1 and remark like '%选定挖机与疑似装载%'")
    List<ProjectCarWorkInfo> getAllByProjectIdAndDateIdentificationAndShiftAndRemark();
}

