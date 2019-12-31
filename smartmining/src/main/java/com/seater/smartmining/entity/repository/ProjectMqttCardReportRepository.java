package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectMqttCardReport;
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
 * @Date 2019/11/3 0003 14:51
 */
public interface ProjectMqttCardReportRepository extends JpaRepository<ProjectMqttCardReport, Long>, JpaSpecificationExecutor<ProjectMqttCardReport> {
    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_mqtt_card_report where project_id = ?1 and datediff(date_identification, ?2) = 0 and shift = ?3")
    void deleteAllByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift);

    @Query(nativeQuery = true, value = "select * from project_mqtt_card_report where project_id = ?1 and datediff(date_identification, ?2) = 0 and shift = ?3" +
            " order by creat_time desc")
    List<ProjectMqttCardReport> getAllByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift);

    @Query(nativeQuery = true, value = "select * from project_mqtt_card_report where project_id = ?1 and car_code = ?2 and time_discharge >= ?3 and time_discharge <= ?4")
    List<ProjectMqttCardReport> getAllByProjectIdAndCarCodeAndTimeDischarge(Long projectId, String carCode, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select * from project_mqtt_card_report where project_id = ?1 and car_code = ?2 and datediff(date_identification, ?3) = 0 and shift = ?4")
    List<ProjectMqttCardReport> getAllByProjectIdAndCarCodeAndDateIdentificationAndShift(Long projectId, String carCode, Date date, Integer shift);

    @Query(nativeQuery = true, value = "select * from project_mqtt_card_report where project_id = ?1 and time_discharge >= ?2 and time_discharge <= ?3")
    List<ProjectMqttCardReport> getAllByProjectIdAndTimeDischarge(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select count(*) as `count`, car_code, error_code, message, dispatch_mode from project_mqtt_card_report" +
            " where project_id = ?1 and datediff(date_identification, ?2) = 0 and shift = ?3 group by car_code, error_code, message, dispatch_mode" +
            " order by car_code")
    List<Map> getReportCountByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift);

    @Query(nativeQuery = true, value = "select count(*) as `count`, car_code, error_code, error_code_message from project_mqtt_card_report" +
            " where project_id = ?1 and datediff(date_identification, ?2) = 0 and shift = ?3 and merge_error = 2 group by car_code, error_code, error_code_message" +
            " order by car_code")
    List<Map> getErrorCountByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift);

    @Query(nativeQuery = true, value = "select distinct error_code from project_mqtt_card_report" +
            " where project_id = ?1 and datediff(date_identification, ?2) = 0 and shift = ?3")
    List<Map> getErrorCodeByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift);

    @Query(nativeQuery = true, value = "select count(*) as count from project_mqtt_card_report" +
            " where project_id = ?1 and car_code = ?2 and datediff(date_identification, ?3) = 0 and shift = ?4")
    Map getTotalCountByProjectIdAndCarCodeAndDateIdentificationAndShift(Long projectId, String carCode, Date date, Integer shift);

    @Query(nativeQuery = true, value = "select count(*) as count, date_identification from project_mqtt_card_report" +
            " where project_id = ?1 and date_identification >= ?2 and date_identification <= ?3" +
            " group by date_identification")
    List<Map> getUnValidCountByProjectIdAndDateIdentification(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select count(*) as count, date_format(date_identification, '%Y-%m') as date_identification from project_mqtt_card_report" +
            " where project_id = ?1 and date_identification >= ?2 and date_identification <= ?3" +
            " group by date_format(date_identification, '%Y-%m')")
    List<Map> getUnValidCountMonthByProjectIdAndDateIdentification(Long projectId, Date startTime, Date endTime);
}
