package com.seater.smartmining.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectScheduleModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/15 0015 10:51
 */
public interface ProjectScheduleModelServiceI {

    ProjectScheduleModel get(Long id) throws IOException;
    ProjectScheduleModel save(ProjectScheduleModel log) throws JsonProcessingException;
    void delete(Long id);
    Page<ProjectScheduleModel> query();
    Page<ProjectScheduleModel> query(Specification<ProjectScheduleModel> spec);
    Page<ProjectScheduleModel> query(Pageable pageable);
    Page<ProjectScheduleModel> query(Specification<ProjectScheduleModel> spec, Pageable pageable);
    List<ProjectScheduleModel> getAll();
    ProjectScheduleModel getAllByProjectIdAndGroupCode(Long projectId, String groupCode);
    void deleteByGroupCode(String groupCode);
    void batchSave(List<ProjectScheduleModel> saveList);
    List<ProjectScheduleModel> getAllByProjectId(Long projectId);
    List<ProjectScheduleModel> getAllByProjectIdAndProgrammeId(Long projectId, Long programmeId);
    void deleteByGroupCodes(List<String> groupCodes);
    List<ProjectScheduleModel> getAllByQuery(Specification<ProjectScheduleModel> spec);
}
