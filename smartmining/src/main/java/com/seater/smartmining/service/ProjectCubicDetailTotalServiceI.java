package com.seater.smartmining.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectCubicDetailTotal;
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
 * @Date 2019/3/4 0004 16:57
 */
public interface ProjectCubicDetailTotalServiceI {

    ProjectCubicDetailTotal get(Long id) throws IOException;
    ProjectCubicDetailTotal save(ProjectCubicDetailTotal log) throws JsonProcessingException;
    void delete(Long id);
    Page<ProjectCubicDetailTotal> query();
    Page<ProjectCubicDetailTotal> query(Specification<ProjectCubicDetailTotal> spec);
    Page<ProjectCubicDetailTotal> query(Pageable pageable);
    Page<ProjectCubicDetailTotal> query(Specification<ProjectCubicDetailTotal> spec, Pageable pageable);
    List<ProjectCubicDetailTotal> getAll();
    void  deleteByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machineId);
    List<ProjectCubicDetailTotal> getAllByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machineId);
}
