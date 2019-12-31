package com.seater.smartmining.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectCubicDetail;
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
 * @Date 2019/3/2 0002 14:05
 */
public interface ProjectCubicDetailDaoI {

    ProjectCubicDetail get(Long id) throws IOException;
    ProjectCubicDetail save(ProjectCubicDetail log) throws JsonProcessingException;
    void delete(Long id);
    Page<ProjectCubicDetail> query();
    Page<ProjectCubicDetail> query(Specification<ProjectCubicDetail> spec);
    Page<ProjectCubicDetail> query(Pageable pageable);
    Page<ProjectCubicDetail> query(Specification<ProjectCubicDetail> spec, Pageable pageable);
    List<ProjectCubicDetail> getAll();
    void deleteByProjectIdAndCreateDateAndMachineId(Long projectId, Date createDate, Long machineId);
    List<ProjectCubicDetail> getAllByProjectIdAndTotalId(Long projectId, Long totalId, Date reportDate);
    List<Map> getReportDateByProjectIdAndCarIdAndTotalId(Long projectId, Long machineId, Long totalId);
    List<Map> getByProjectIdAndReportDate(Long projectId, Date startTime, Date endTime);
    List<Map> getByProjectIdAndReportDateAndMachineId(Long projectId, Date startTime, Date endTime, Long machineId);
    List<Map> getByProjectIdAndDate(Long projectId, Date startTime, Date endTime);
}
