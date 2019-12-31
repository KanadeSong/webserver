package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectDayReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ProjectDayReportDaoI {
    ProjectDayReport get(Long id) throws IOException;
    ProjectDayReport save(ProjectDayReport log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectDayReport> query();
    Page<ProjectDayReport> query(Specification<ProjectDayReport> spec);
    Page<ProjectDayReport> query(Pageable pageable);
    Page<ProjectDayReport> query(Specification<ProjectDayReport> spec, Pageable pageable);
    List<ProjectDayReport> getAll();
    ProjectDayReport getByProjectIdAndReportDate(Long projectId, Date reportDate);
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate);
    Map getOnDutyCountByProjectIdAndReportDate(Long projectId, Date startTime, Date endTime);
    List<ProjectDayReport> getSettlementDetailByProjectIdAndTime(Long projectId, Date startTime, Date endTime);
    List<ProjectDayReport> getByProjectIdAndCreateDate(Long projectId, Date createDate);
    List<Map> getAvgCarInfo(Long projectId, Date startTime, Date endTime);
    List<Map> getAvgCarInfoMonth(Long projectId, Date startTime, Date endTime);
}
