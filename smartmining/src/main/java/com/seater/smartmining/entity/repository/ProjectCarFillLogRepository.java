package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectCarFillLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ProjectCarFillLogRepository extends JpaRepository<ProjectCarFillLog, Long>, JpaSpecificationExecutor<ProjectCarFillLog> {
    @Query("SELECT p FROM ProjectCarFillLog p where p.carType = 3 and p.projectId = ?1 and p.date >= ?2 and p.date <= ?3 ORDER BY p.carCode")
    List<ProjectCarFillLog> getByProjectIdAndTime(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select car_id, car_code, sum(volumn) as totalFill, sum(amount) as totalAmount  from project_car_fill_log " +
            "where car_type = 3 and project_id = ?1 and date >= ?2 and date <= ?3 group by car_id, car_code order by car_id")
    List<Map> getCarGrandTotalFillByProjectIdAndTime(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select sum(volumn) as totalFill, sum(amount) as totalAmount  from project_car_fill_log " +
            "where car_code = ?1 and car_type = 2 and project_id = ?2 and date >= ?3 and date <= ?4")
    List<Map> getDiggingGrandTotalFillByProjectIdAndTime(String code, Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select car_id, car_code,sum(volumn) as totalFill, sum(amount) as totalAmount, pricing_type_enums from project_car_fill_log " +
            "where car_type = 2 and project_id = ?1 and date >= ?2 and date <= ?3 group by car_id, car_code, pricing_type_enums")
    List<Map> getDiggingTotalFillByProjectIdAndTimeGroupByCar(Long projectId, Date startTime, Date endTime);

    //todo  修改中
    @Query(nativeQuery = true, value = "select sum(volumn) as totalFill, sum(amount) as totalAmount, pricing_type_enums from project_car_fill_log " +
            " where car_type = 2 and project_id = ?1 and date >= ?2 and date <= ?3 group by pricing_type_enums")
    List<Map> getDiggingTotalFillByProjectIdAndTime(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select sum(volumn) as totalFill, sum(amount) as totalAmount, pricing_type_enums from project_car_fill_log " +
            " where car_type = 2 and project_id = ?1 group by pricing_type_enums")
    List<Map> getHistoryDiggingTotalFillByProjectId(Long projectId);

    @Query(nativeQuery = true, value = "select sum(volumn) as totalFill, sum(amount) as totalAmount  from project_car_fill_log" +
            " where car_type = 3 and project_id = ?1 and car_id = ?2 and date >= ?3 and date <= ?4")
    Map getCarFillByProjectIdAAndCarIdAndTime(Long projectId, Long carId, Date beginDate, Date endDate);

    @Query(nativeQuery = true, value = "select sum(volumn) as totalFill, sum(amount) as totalAmount  from project_car_fill_log" +
            " where car_type = 3 and project_id = ?1 and car_id = ?2 and datediff(date, ?3) = 0")
    Map getCarFillByProjectIdAndCarIdAndTime(Long projectId, Long carId, Date date);

    @Query(nativeQuery = true, value = "select sum(volumn) as totalFill, sum(amount) as totalAmount  from project_car_fill_log" +
            " where car_type = 2 and project_id = ?1 and car_id = ?2 and date >= ?3 and date <= ?4")
    Map getDiggingFillByProjectIdAndCarIdAndTime(Long projectId, Long carId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select distinct car_id from project_car_fill_log " +
            "where car_type = 2 and project_id = ?1 and date >= ?2 and date <= ?3")
    List<Map> getDiggingMachineIdByProjectIdAndTime(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select sum(volumn) as volumn, sum(amount) as amount from project_car_fill_log" +
            " where project_id = ?1 and date <= ?2 and car_type = 3")
    Map getAllByProjectIdAndDate(Long projectId, Date reportDate);

    @Query(nativeQuery = true, value = "select sum(volumn) as volumn, sum(amount) as amount, pricing_type_enums from project_car_fill_log" +
            " where project_id = ?1 and date <= ?2 and car_type = 2 group by pricing_type_enums")
    List<Map> getAllByProjectIdAndDateAndCarType(Long projectId, Date reportDate);

    @Query(nativeQuery = true, value = "select sum(volumn) / 1000 as volumn, pricing_type_enums, date_identification from project_car_fill_log" +
            " where project_id = ?1 and date_identification >=?2 and date_identification <=?3 and car_type = ?4 group by date_identification,pricing_type_enums")
    List<Map> getFillLogReport(Long projectId, Date startTime, Date endTime, Integer carType);

    @Query(nativeQuery = true, value = "select sum(volumn) / 1000 as volumn, pricing_type_enums, date_format(date_identification, '%Y-%m') as date_identification from project_car_fill_log" +
            " where project_id = ?1 and date_identification >=?2 and date_identification <=?3 and car_type = ?4 group by date_format(date_identification, '%Y-%m'),pricing_type_enums")
    List<Map> getFillLogReportMonth(Long projectId, Date startTime, Date endTime, Integer carType);

    @Query(nativeQuery = true, value = "select sum(volumn) / 1000 as volumn, pricing_type_enums, date_format(date_identification, '%Y-%m') as date_identification from project_car_fill_log" +
            " where project_id = ?1 and date_identification <=?2 and car_type = ?3 group by date_format(date_identification, '%Y-%m'),pricing_type_enums")
    List<Map> getFillLogReportHistory(Long projectId, Date endTime, Integer carType);

    @Query(nativeQuery = true, value = "select sum(volumn) as volumn, sum(amount) as amount, date_identification from project_car_fill_log" +
            " where project_id = ?1 and datediff(date_identification, ?2) = 0 and car_type = ?3 group by date_identification")
    List<Map> getFillLogOnCar(Long projectId, Date date, Integer carType);

    @Query(nativeQuery = true, value = "select sum(volumn) as volumn, sum(amount) as amount, date_format(date_identification, '%Y-%m') as date_identification from project_car_fill_log" +
            " where project_id = ?1 and date_identification >= ?2 and date_identification <= ?3 and car_type = ?4" +
            " group by date_format(date_identification, '%Y-%m')")
    List<Map> getFillLogOnCarMonth(Long projectId, Date startTime, Date endTime,Integer carType);
}
