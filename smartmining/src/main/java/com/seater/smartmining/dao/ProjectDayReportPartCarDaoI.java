package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectDayReport;
import com.seater.smartmining.entity.ProjectDayReportPartCar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ProjectDayReportPartCarDaoI {
    ProjectDayReportPartCar get(Long id) throws IOException;
    ProjectDayReportPartCar save(ProjectDayReportPartCar log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectDayReportPartCar> query();
    Page<ProjectDayReportPartCar> query(Specification<ProjectDayReportPartCar> spec);
    Page<ProjectDayReportPartCar> query(Pageable pageable);
    Page<ProjectDayReportPartCar> query(Specification<ProjectDayReportPartCar> spec, Pageable pageable);
    List<ProjectDayReportPartCar> getAll();
    List<ProjectDayReportPartCar> getByReportIdOrderByCarCode(Long reportId);
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate);
    ProjectDayReportPartCar getByProjectIdAndReportDate(Long projectId, Date reportDate);
    List<Map> getMonthReportByProjectIdAndReportDate(Long projectId, String startTime, String endTime);
    /*List<Map> getMonthReportByProjectIdAndReportDate(Long projectId, Date startTime, Date endTime);*/
    Map getTotalInfoByProjectIdAndReportDate(Long projectId, Date reportDate);
    Map getGrandInfoByProjectIdAndReportDate(Long projectId, Date reportDate);
    Map getHistoryInfoByProjectId(Long projectId);
    Map getSettlementDetailByProjectIdAndReportIdAndCarId(Long projectId, Long reportId, Long carId);
    List<Map> getMonthCarCountByProjectIdAndReportDate(Long projectId, Date startTime, Date endTime);
    List<ProjectDayReportPartCar> queryWx(Specification<ProjectDayReportPartCar> spec);
}
