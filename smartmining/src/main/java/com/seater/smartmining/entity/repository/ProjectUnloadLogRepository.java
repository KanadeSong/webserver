package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectUnloadLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ProjectUnloadLogRepository extends JpaRepository<ProjectUnloadLog, Long>, JpaSpecificationExecutor<ProjectUnloadLog> {
    @Query("select max(timeDischarge) from ProjectUnloadLog where carCode = ?1 and isVaild = true and timeDischarge <= ?2")
    List<Date> getMaxUnloadDateByCarCode(String carCode, Date date);

    @Query(nativeQuery = true,value = "select * from project_unload_log where recvice_date > ?1")
    List<ProjectUnloadLog> getAllByRecviceDate(Date receiveDate);

    @Query(nativeQuery = true, value = "select * from project_unload_log where projectid = ?1 and time_discharge >= ?2 and time_discharge <= ?3 " +
            " and uid = ?4 and is_vaild =true")
    List<ProjectUnloadLog> getAllByProjectIDAndTimeDischarge(Long projectId, Date startDate, Date endDate, String uid);

    @Query(nativeQuery = true, value = "select * from project_unload_log where projectid = ?1 and time_discharge >= ?2 and time_discharge <= ?3 and is_vaild = true")
    List<ProjectUnloadLog> getAllByProjectIDAndTime(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select carid,car_code, count(*) as count from project_unload_log where projectid = ?1 and time_discharge >= ?2 and time_discharge <= ?3" +
            " and is_vaild = true group by car_code, carid")
    List<Map> getCarCodeByProjectIDAndTime(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select car_code, count(*) as count from project_unload_log where projectid = ?1 and time_discharge >= ?2 and time_discharge <= ?3" +
            " and is_vaild = true and time_load != ?4 group by car_code")
    List<Map> getCarCountByProjectIDAndTime(Long projectId, Date startTime, Date endTime, Date uploadTime);

    @Query(nativeQuery = true, value = "select car_code, count(*) as count from project_unload_log where projectid = ?1 and time_discharge >= ?2 and time_discharge <= ?3" +
            " and is_vaild = false group by car_code")
    List<Map> getUnValidByProjectIDAndTime(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select car_code, count(*) as count from project_unload_log where projectid = ?1" +
            " and time_discharge >= ?2 and time_discharge <= ?3 and is_vaild = true and time_check != ?4" +
            " group by car_code")
    List<Map> getUploadCountByCheck(Long projectId, Date startTime, Date endTime, Date checkTime);

    @Query(nativeQuery = true, value = "select count(*) as `count`, carid, car_code, slagfieldid from project_unload_log" +
            " where projectid = ?1 and time_discharge >= ?2 and time_discharge <= ?3 and is_vaild = true" +
            " group by carid, car_code, slagfieldid order by car_code")
    List<Map> getReportInfoGroup(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select count(*) as `count`, carid, car_code, slagfieldid from project_unload_log" +
            " where projectid = ?1 and time_discharge >= ?2 and time_discharge <= ?3 and is_vaild = true and slagfieldid in ?4" +
            " group by carid, car_code, slagfieldid order by car_code")
    List<Map> getReportInfoGroupBySlagSite(Long projectId, Date startTime, Date endTime, List<Long> ids);

    @Query(nativeQuery = true, value = "select count(*) as `count`, carid, car_code from project_unload_log" +
            " where projectid = ?1 and time_discharge >= ?2 and time_discharge <= ?3 and is_vaild = true" +
            " group by carid, car_code order by car_code")
    List<Map> getTotalReportInfoByCarCode(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select count(*) as `count`, carid, car_code from project_unload_log" +
            " where projectid = ?1 and time_discharge >= ?2 and time_discharge <= ?3 and is_vaild = true and slagfieldid in ?4" +
            " group by carid, car_code order by car_code")
    List<Map> getTotalReportInfoByCarCodeAndSlagSite(Long projectId, Date startTime, Date endTime, List<Long> ids);

    @Query(nativeQuery = true, value = "select count(*) as `count` from project_unload_log" +
            " where projectid = ?1 and time_discharge >= ?2 and time_discharge <= ?3 and is_vaild = true")
    Map getTotalCountByProjectIDAndTimeDischarge(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select * from project_unload_log" +
            " where projectid = ?1 and time_discharge >= ?2 and time_discharge <= ?3 and is_vaild = ?4 and detail = ?5")
    List<ProjectUnloadLog> getAllByProjectIDAndTimeDischargeAndIsVaildAndDetail(Long projectId, Date startTime, Date endTime, Boolean valid, Boolean detail);

    /**
     * 数据恢复使用
     * @param projectId
     * @param timeDischarge
     * @param carCode
     * @return
     */
    @Query(nativeQuery = true, value = "select * from project_unload_log where projectid = ?1" +
            " and time_discharge = ?2 and car_code = ?3 and is_vaild = true")
    ProjectUnloadLog getAllByProjectIDAndTimeDischargeAndCarCode(Long projectId, Date timeDischarge, String carCode);

    @Query(nativeQuery = true, value = "select * from project_unload_log where time_discharge >= '2019-12-13 05:30:00'" +
            " and time_discharge <= '2019-12-13 17:29:59' and is_vaild = true and projectid = 1")
    List<ProjectUnloadLog> getAllByProjectIDAndTimeDischargeAndIsVaild();
}
