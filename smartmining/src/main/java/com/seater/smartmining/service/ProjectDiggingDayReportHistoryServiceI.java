package com.seater.smartmining.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectDiggingDayReportHistory;
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
 * @Date 2019/6/11 0011 16:35
 */
public interface ProjectDiggingDayReportHistoryServiceI {
    ProjectDiggingDayReportHistory get(Long id) throws IOException;
    ProjectDiggingDayReportHistory save(ProjectDiggingDayReportHistory log) throws JsonProcessingException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectDiggingDayReportHistory> query();
    Page<ProjectDiggingDayReportHistory> query(Specification<ProjectDiggingDayReportHistory> spec);
    Page<ProjectDiggingDayReportHistory> query(Pageable pageable);
    Page<ProjectDiggingDayReportHistory> query(Specification<ProjectDiggingDayReportHistory> spec, Pageable pageable);
    List<ProjectDiggingDayReportHistory> getAll();
    ProjectDiggingDayReportHistory getAllByProjectIdAndReportDate(Long projectId, Date reportDate);
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate);
}
