package com.seater.smartmining.service;

import com.seater.smartmining.entity.ProjectCarFillLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ProjectCarFillLogServiceI {
    ProjectCarFillLog get(Long id) throws IOException;
    ProjectCarFillLog save(ProjectCarFillLog log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectCarFillLog> query();
    Page<ProjectCarFillLog> query(Specification<ProjectCarFillLog> spec);
    Page<ProjectCarFillLog> query(Pageable pageable);
    Page<ProjectCarFillLog> query(Specification<ProjectCarFillLog> spec, Pageable pageable);
    List<ProjectCarFillLog> getAll();
    List<ProjectCarFillLog> getByProjectIdAndTime(Long projectId, Date startTime, Date endTime);
    List<Map> getCarGrandTotalFillByProjectIdAndTime(Long projectId, Date startTime, Date endTime);
    List<Map> getDiggingGrandTotalFillByProjectIdAndTime(String code, Long projectId, Date startTime, Date endTime);
    List<Map> getDiggingTotalFillByProjectIdAndTimeGroupByCar(Long projectId, Date startTime, Date endTime);
    List<Map> getDiggingTotalFillByProjectIdAndTime(Long projectId, Date startTime, Date endTime);
    List<Map> getHistoryDiggingTotalFillByProjectId(Long projectId);
    Map getCarFillByProjectIdAAndCarIdAndTime(Long projectId, Long carId, Date beginDate, Date endDate);
    Map getCarFillByProjectIdAndCarIdAndTime(Long projectId, Long carId, Date date);
    List<ProjectCarFillLog> queryWx(Specification<ProjectCarFillLog> spec);
    Map getDiggingFillByProjectIdAndCarIdAndTime(Long projectId, Long carId, Date startTime, Date endTime);
    List<Map> getDiggingMachineIdByProjectIdAndTime(Long projectId, Date startTime, Date endTime);
    Map getAllByProjectIdAndDate(Long projectId, Date reportDate);
    List<Map> getAllByProjectIdAndDateAndCarType(Long projectId, Date reportDate);
    List<Map> getFillLogReport(Long projectId, Date startTime, Date endTime, Integer carType);
    List<Map> getFillLogReportMonth(Long projectId, Date startTime, Date endTime, Integer carType);
    List<Map> getFillLogReportHistory(Long projectId, Date endTime, Integer carType);
    List<Map> getFillLogOnCar(Long projectId, Date date, Integer carType);
    List<Map> getFillLogOnCarMonth(Long projectId, Date startTime, Date endTime,Integer carType);
}
