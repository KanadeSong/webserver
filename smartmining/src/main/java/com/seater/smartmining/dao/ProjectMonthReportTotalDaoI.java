package com.seater.smartmining.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectMonthReportTotal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/2/19 0019 10:50
 */
public interface ProjectMonthReportTotalDaoI {

    ProjectMonthReportTotal get(Long id) throws IOException;
    ProjectMonthReportTotal save(ProjectMonthReportTotal log) throws JsonProcessingException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectMonthReportTotal> query();
    Page<ProjectMonthReportTotal> query(Specification<ProjectMonthReportTotal> spec);
    Page<ProjectMonthReportTotal> query(Pageable pageable);
    Page<ProjectMonthReportTotal> query(Specification<ProjectMonthReportTotal> spec, Pageable pageable);
    List<ProjectMonthReportTotal> getAll();
    List<ProjectMonthReportTotal> getByProjectIdAndReportDate(Long projectId, Date reportDate);
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate);
    void setDeductionAndSubsidyAmount(Long id, Long deduction, Long subsidyAmount);
}
