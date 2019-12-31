package com.seater.smartmining.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/23 0023 14:21
 */
public interface ProjectScheduleServiceI {

    ProjectSchedule get(Long id) throws IOException;
    ProjectSchedule save(ProjectSchedule log) throws JsonProcessingException;
    void delete(Long id);
    Page<ProjectSchedule> query();
    Page<ProjectSchedule> query(Specification<ProjectSchedule> spec);
    Page<ProjectSchedule> query(Pageable pageable);
    Page<ProjectSchedule> query(Specification<ProjectSchedule> spec, Pageable pageable);
    List<ProjectSchedule> getAll();
    void deleteByProjectIdAndGroupCode(Long projectId, String groupCode);
    List<ProjectSchedule> getAllByProjectId(Long projectId);
    List<ProjectSchedule> getAllByProjectIdAndManagerId(Long projectId, String managerId, Integer current, Integer pageSize);
    ProjectSchedule getAllByProjectIdAndGroupCode(Long projectId, String groupCode);
    List<ProjectSchedule> getAllByProjectIdAndManagerIdOrderById(Long projectId, String managerId);
    List<ProjectSchedule> getAllByQuery(Specification<ProjectSchedule> spec);
    void deleteByGroupCode(String groupCode);
    void batchSave(List<ProjectSchedule> scheduleList);
    List<Map> getAllDistinctByProjectId(Long projectId);
    void deleteAll(Long projectId);
}
