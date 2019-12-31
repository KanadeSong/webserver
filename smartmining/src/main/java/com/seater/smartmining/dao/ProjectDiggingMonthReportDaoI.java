package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectDiggingMonthReport;
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
 * @Date 2019/1/28 0028 18:37
 */
public interface ProjectDiggingMonthReportDaoI {

    ProjectDiggingMonthReport get(Long id) throws IOException;
    ProjectDiggingMonthReport save(ProjectDiggingMonthReport log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectDiggingMonthReport> query();
    Page<ProjectDiggingMonthReport> query(Specification<ProjectDiggingMonthReport> spec);
    Page<ProjectDiggingMonthReport> query(Pageable pageable);
    Page<ProjectDiggingMonthReport> query(Specification<ProjectDiggingMonthReport> spec, Pageable pageable);
    List<ProjectDiggingMonthReport> getAll();
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDay);
    List<ProjectDiggingMonthReport> getAllByProjectId(Long projectId);
    List<ProjectDiggingMonthReport> getByProjectIdAndReportDate(Long projectId, Date reportDate);
    List<ProjectDiggingMonthReport> getByTotalId(Long totalId);
    void setDeductionAndSubsidyAmount(Long id, Long deduction, Long subsidyAmount, Long workTotalAmount);
    List<ProjectDiggingMonthReport> getByTotalIdAndOwnerId(Long totalId, Long ownerId);
    List<ProjectDiggingMonthReport> saveAll(List<ProjectDiggingMonthReport> saveList);
}
