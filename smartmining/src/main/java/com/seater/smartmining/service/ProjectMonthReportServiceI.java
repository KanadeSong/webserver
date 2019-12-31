package com.seater.smartmining.service;

import com.seater.smartmining.entity.ProjectMonthReport;
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
 * @Date 2019/2/19 0019 10:47
 */
public interface ProjectMonthReportServiceI {

    ProjectMonthReport get(Long id) throws IOException;
    ProjectMonthReport save(ProjectMonthReport log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectMonthReport> query();
    Page<ProjectMonthReport> query(Specification<ProjectMonthReport> spec);
    Page<ProjectMonthReport> query(Pageable pageable);
    Page<ProjectMonthReport> query(Specification<ProjectMonthReport> spec, Pageable pageable);
    List<ProjectMonthReport> getAll();
    List<ProjectMonthReport> getByTotalId(Long totalId);
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDay);
    void setDeductionAndSubsidyAmount(Long id, Long deduction, Long subsidyAmount);
    List<ProjectMonthReport> getByTotalIdAndCarIdIn(Long totalId, List<Long> carIds);
    ProjectMonthReport getAllByProjectIdAndCarIdAndReportDate(Long projectId, Long carId, Date reportDate);
}
