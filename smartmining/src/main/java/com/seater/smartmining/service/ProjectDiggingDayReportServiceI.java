package com.seater.smartmining.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectDiggingDayReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/2/12 0012 16:40
 */
public interface ProjectDiggingDayReportServiceI {
    ProjectDiggingDayReport get(Long id) throws IOException;
    ProjectDiggingDayReport save(ProjectDiggingDayReport log) throws JsonProcessingException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectDiggingDayReport> query();
    Page<ProjectDiggingDayReport> query(Specification<ProjectDiggingDayReport> spec);
    Page<ProjectDiggingDayReport> query(Pageable pageable);
    Page<ProjectDiggingDayReport> query(Specification<ProjectDiggingDayReport> spec, Pageable pageable);
    List<ProjectDiggingDayReport> getAll();
    List<ProjectDiggingDayReport> getByTotalId(Long totalId);
    List<Map> getMonthReportByProjectIdAndReportDate(Long projectId, Date startDate, Date endDate);
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate);
    Map getTotalInfoByProjectIdAndTime(Long projectId, Date reportDate);
    Map getGrandInfoByProjectIdAndTime(Long projectId, Date reportDate);
    Map getHistoryInfoByProjectId(Long projectId);
    void setDeductionTimeByDayAndDeductionTimeByNightOrderById(Long id, BigDecimal deductionTimeByDay, BigDecimal deductionTimeByNight);
    List<Map> getCubicDetailByProjectIdAndReportDateAndMachineId(Long projectId, Date startTime, Date endTime, Long machineId);
    List<ProjectDiggingDayReport> queryWx(Specification<ProjectDiggingDayReport> spec);
    List<ProjectDiggingDayReport> getAllByProjectIdAndMachineIdAndReportDate(Long projectId, Long machineId, Date reportDate);
    void batchSave(List<ProjectDiggingDayReport> reportList);
}
