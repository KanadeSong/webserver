package com.seater.smartmining.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectCarCostAccounting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/2/22 0022 9:36
 */
public interface ProjectCarCostAccountingDaoI {

    ProjectCarCostAccounting get(Long id) throws IOException;
    ProjectCarCostAccounting save(ProjectCarCostAccounting log) throws JsonProcessingException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectCarCostAccounting> query();
    Page<ProjectCarCostAccounting> query(Specification<ProjectCarCostAccounting> spec);
    Page<ProjectCarCostAccounting> query(Pageable pageable, Specification<ProjectCarCostAccounting> spec);
    Page<ProjectCarCostAccounting> query(Pageable pageable);
    List<ProjectCarCostAccounting> getAll();
    List<ProjectCarCostAccounting> getAllByProjectIdAndReportDate(Long projectId, Date reportDate);
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate);
    List<Map> getCarAmountReport(Long projectId, Date startTime, Date endTime);
    List<Map> getCarAmountReportMonth(Long projectId, Date startTime, Date endTime);
    List<Date> getMaxReportDate();
    List<ProjectCarCostAccounting> getAllByProjectIdAndTime(Long projectId, Date startTime, Date endTime);
    List<Map> getHistoryAmount(Long projectId, Date startTime, Date endTime);
    List<Map> getHistoryAmountHistory(Long projectId, Date date);
    List<Map> getHistoryFillAmountAndAmount(Long projectId, Date startTime, Date endTime);
    List<Map> getHistoryFillAmountAndAmountHistory(Long projectId, Date date);
    List<Map> getHistoryFillAmountAndAmountMonth(Long projectId, Date startTime, Date endTime);
    Map getAllByProjectIdAndReportDate(Long projectId, Date startTime, Date endTime);
}
