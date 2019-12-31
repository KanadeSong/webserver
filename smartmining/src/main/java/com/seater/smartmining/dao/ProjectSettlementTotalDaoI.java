package com.seater.smartmining.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectSettlementTotal;
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
 * @Date 2019/3/2 0002 17:48
 */
public interface ProjectSettlementTotalDaoI {

    ProjectSettlementTotal get(Long id) throws IOException;
    ProjectSettlementTotal save(ProjectSettlementTotal log) throws JsonProcessingException;
    void delete(Long id);
    Page<ProjectSettlementTotal> query();
    Page<ProjectSettlementTotal> query(Specification<ProjectSettlementTotal> spec);
    Page<ProjectSettlementTotal> query(Pageable pageable);
    Page<ProjectSettlementTotal> query(Specification<ProjectSettlementTotal> spec, Pageable pageable);
    List<ProjectSettlementTotal> getAll();
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate, Long carId);
    List<ProjectSettlementTotal> getByProjectIdAndCarIdAndReportDate(Long projectId, Long carId, Date reportDate);
}
