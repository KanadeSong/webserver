package com.seater.smartmining.dao;
import com.seater.smartmining.entity.ProjectDiggingMonthReport;
import com.seater.smartmining.entity.ProjectDiggingMonthReportTotal;
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
 * @Date 2019/2/16 0016 13:24
 */
public interface ProjectDiggingMonthReportTotalDaoI {

    ProjectDiggingMonthReportTotal get(Long id) throws IOException;
    ProjectDiggingMonthReportTotal save(ProjectDiggingMonthReportTotal log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectDiggingMonthReportTotal> query();
    Page<ProjectDiggingMonthReportTotal> query(Specification<ProjectDiggingMonthReportTotal> spec);
    Page<ProjectDiggingMonthReportTotal> query(Pageable pageable);
    Page<ProjectDiggingMonthReportTotal> query(Specification<ProjectDiggingMonthReportTotal> spec, Pageable pageable);
    List<ProjectDiggingMonthReportTotal> getAll();
    List<ProjectDiggingMonthReportTotal> getByProjectIdAndReportDate(Long projectId, Date reportDate);
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate);
    void setDeductionAndSubsidyAmount(Long id, Long deduction, Long subsidyAmount);
}
