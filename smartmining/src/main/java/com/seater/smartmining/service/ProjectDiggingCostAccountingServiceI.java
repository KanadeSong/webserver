package com.seater.smartmining.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectDiggingCostAccounting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/2/21 0021 9:12
 */
public interface ProjectDiggingCostAccountingServiceI {

    ProjectDiggingCostAccounting get(Long id) throws IOException;
    ProjectDiggingCostAccounting save(ProjectDiggingCostAccounting log) throws JsonProcessingException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectDiggingCostAccounting> query();
    Page<ProjectDiggingCostAccounting> query(Specification<ProjectDiggingCostAccounting> spec);
    Page<ProjectDiggingCostAccounting> query(Pageable pageable);
    Page<ProjectDiggingCostAccounting> query(Specification<ProjectDiggingCostAccounting> spec, Pageable pageable);
    List<ProjectDiggingCostAccounting> getAll();
    List<ProjectDiggingCostAccounting> getAllByProjectIdAndReportDate(Long projectId, Date reportDate);
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate);
    List<Map> getAllByProjectIdAndTime(Long projectId, Date startTime, Date endTime);
    List<Map> getAllByProjectIdAndTimeMonth(Long projectId, Date startTime, Date endTime);
    List<Map> getAllByProjectIdAndTimeHistory(Long projectId, Date endTime);
    Date getMaxReportDate();
    List<Map> getHistoryAmount(Long projectId, Date startTime, Date endTime);
    List<Map> getHistoryAmountHistory(Long projectId, Date endTime);
    List<Map> getHistoryFillAmountAndAmount(Long projectId, Date startTime, Date endTime);
    List<Map> getHistoryFillAmountAndAmountMonth(Long projectId, Date startTime, Date endTime);
    Map getAllByProjectIdAndReportDate(Long projectId, Date startTime, Date endTime);
}
