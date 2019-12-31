package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectScheduleModelDaoI;
import com.seater.smartmining.entity.ProjectScheduleModel;
import com.seater.smartmining.service.ProjectScheduleModelServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/15 0015 10:51
 */
@Service
public class ProjectScheduleModelServiceImpl implements ProjectScheduleModelServiceI {

    @Autowired
    private ProjectScheduleModelDaoI projectScheduleModelDaoI;

    @Override
    public ProjectScheduleModel get(Long id) throws IOException {
        return projectScheduleModelDaoI.get(id);
    }

    @Override
    public ProjectScheduleModel save(ProjectScheduleModel log) throws JsonProcessingException {
        return projectScheduleModelDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectScheduleModelDaoI.delete(id);
    }

    @Override
    public Page<ProjectScheduleModel> query() {
        return projectScheduleModelDaoI.query();
    }

    @Override
    public Page<ProjectScheduleModel> query(Specification<ProjectScheduleModel> spec) {
        return projectScheduleModelDaoI.query(spec);
    }

    @Override
    public Page<ProjectScheduleModel> query(Pageable pageable) {
        return projectScheduleModelDaoI.query(pageable);
    }

    @Override
    public Page<ProjectScheduleModel> query(Specification<ProjectScheduleModel> spec, Pageable pageable) {
        return projectScheduleModelDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectScheduleModel> getAll() {
        return projectScheduleModelDaoI.getAll();
    }

    @Override
    public ProjectScheduleModel getAllByProjectIdAndGroupCode(Long projectId, String groupCode) {
        return projectScheduleModelDaoI.getAllByProjectIdAndGroupCode(projectId, groupCode);
    }

    @Override
    public void deleteByGroupCode(String groupCode) {
        projectScheduleModelDaoI.deleteByGroupCode(groupCode);
    }

    @Override
    public void batchSave(List<ProjectScheduleModel> saveList) {
        projectScheduleModelDaoI.batchSave(saveList);
    }

    @Override
    public List<ProjectScheduleModel> getAllByProjectId(Long projectId) {
        return projectScheduleModelDaoI.getAllByProjectId(projectId);
    }

    @Override
    public List<ProjectScheduleModel> getAllByProjectIdAndProgrammeId(Long projectId, Long programmeId) {
        return projectScheduleModelDaoI.getAllByProjectIdAndProgrammeId(projectId, programmeId);
    }

    @Override
    public void deleteByGroupCodes(List<String> groupCodes) {
        projectScheduleModelDaoI.deleteByGroupCodes(groupCodes);
    }

    @Override
    public List<ProjectScheduleModel> getAllByQuery(Specification<ProjectScheduleModel> spec) {
        return projectScheduleModelDaoI.getAllByQuery(spec);
    }
}
