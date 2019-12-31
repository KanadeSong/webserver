package com.seater.smartmining.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectSettlementDetail;
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
 * @Date 2019/3/2 0002 14:08
 */
public interface ProjectSettlementDetailDaoI {

    ProjectSettlementDetail get(Long id) throws IOException;
    ProjectSettlementDetail save(ProjectSettlementDetail log) throws JsonProcessingException;
    void delete(Long id);
    Page<ProjectSettlementDetail> query();
    Page<ProjectSettlementDetail> query(Specification<ProjectSettlementDetail> spec);
    Page<ProjectSettlementDetail> query(Pageable pageable);
    Page<ProjectSettlementDetail> query(Specification<ProjectSettlementDetail> spec, Pageable pageable);
    List<ProjectSettlementDetail> getAll();
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate, Long carId);
    List<ProjectSettlementDetail> getByProjectIdAndTotalId(Long projectId, Long totalId, Date reportDate);
    List<Map> getReportDateByProjectIdAndCarIdAndTotalId(Long projectId, Long carId, Long totalId);
    List<Map> getTotalInfoByTotalId(Long totalId);
}
