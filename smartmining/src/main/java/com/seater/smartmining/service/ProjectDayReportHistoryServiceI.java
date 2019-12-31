package com.seater.smartmining.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectDayReportHistory;
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
 * @Date 2019/5/7 0007 16:25
 */
public interface ProjectDayReportHistoryServiceI {

    ProjectDayReportHistory get(Long id) throws IOException;
    ProjectDayReportHistory save(ProjectDayReportHistory log) throws JsonProcessingException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectDayReportHistory> query();
    Page<ProjectDayReportHistory> query(Specification<ProjectDayReportHistory> spec);
    Page<ProjectDayReportHistory> query(Pageable pageable);
    Page<ProjectDayReportHistory> query(Specification<ProjectDayReportHistory> spec, Pageable pageable);
    List<ProjectDayReportHistory> getAll();
    ProjectDayReportHistory getAllByProjectIdAndReportDate(Long projectId, Date reportDate);
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate);
}
