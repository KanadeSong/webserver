package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectCarFillMeterReadingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ProjectCarFillMeterReadingLogRepository extends JpaRepository<ProjectCarFillMeterReadingLog, Long>, JpaSpecificationExecutor<ProjectCarFillMeterReadingLog> {

    ProjectCarFillMeterReadingLog getByProjectIdAndOilCarIdAndAddTime(Long projectId, Long oilCarId, Date addTime);
    
    @Query(nativeQuery = true,value = "SELECT\n" +
            "	IFNULL( SUM( oil_meter_today_total ), 0 ) \n" +
            "FROM\n" +
            "	project_car_fill_meter_reading_log\n" +
            "WHERE\n" +
            "	oil_car_id = ?1")
    Long getHistoryByOilCarId(Long oidCarId);
}
