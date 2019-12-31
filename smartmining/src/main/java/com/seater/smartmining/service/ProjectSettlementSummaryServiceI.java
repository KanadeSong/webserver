package com.seater.smartmining.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectSettlementSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/2 0002 17:40
 */
public interface ProjectSettlementSummaryServiceI {

    ProjectSettlementSummary get(Long id) throws IOException;
    ProjectSettlementSummary save(ProjectSettlementSummary log) throws JsonProcessingException;
    void delete(Long id);
    Page<ProjectSettlementSummary> query();
    Page<ProjectSettlementSummary> query(Specification<ProjectSettlementSummary> spec);
    Page<ProjectSettlementSummary> query(Pageable pageable);
    Page<ProjectSettlementSummary> query(Specification<ProjectSettlementSummary> spec, Pageable pageable);
    List<ProjectSettlementSummary> getAll();
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate, Long carId);
    List<ProjectSettlementSummary> getByProjectIdAndTotalId(Long projectId, Long totalId, Date reportDate);
}
