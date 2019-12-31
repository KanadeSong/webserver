package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectScheduleDaoI;
import com.seater.smartmining.entity.ProjectSchedule;
import com.seater.smartmining.service.ProjectScheduleServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/23 0023 14:21
 */
@Service
public class ProjectScheduleServiceImpl implements ProjectScheduleServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectScheduleDaoI projectScheduleDaoI;

    @Override
    public ProjectSchedule get(Long id) throws IOException {
        return projectScheduleDaoI.get(id);
    }

    @Override
    public ProjectSchedule save(ProjectSchedule log) throws JsonProcessingException {
        return projectScheduleDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectScheduleDaoI.delete(id);
    }

    @Override
    public Page<ProjectSchedule> query() {
        return projectScheduleDaoI.query();
    }

    @Override
    public Page<ProjectSchedule> query(Specification<ProjectSchedule> spec) {
        return projectScheduleDaoI.query(spec);
    }

    @Override
    public Page<ProjectSchedule> query(Pageable pageable) {
        return projectScheduleDaoI.query(pageable);
    }

    @Override
    public Page<ProjectSchedule> query(Specification<ProjectSchedule> spec, Pageable pageable) {
        return projectScheduleDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectSchedule> getAll() {
        return projectScheduleDaoI.getAll();
    }

    @Override
    public void deleteByProjectIdAndGroupCode(Long projectId, String groupCode) {
        projectScheduleDaoI.deleteByProjectIdAndGroupCode(projectId, groupCode);
    }

    @Override
    public List<ProjectSchedule> getAllByProjectId(Long projectId) {
        return projectScheduleDaoI.getAllByProjectId(projectId);
    }

    @Override
    public List<ProjectSchedule> getAllByProjectIdAndManagerId(Long projectId, String managerId, Integer current, Integer pageSize) {
        return projectScheduleDaoI.getAllByProjectIdAndManagerId(projectId, managerId, current, pageSize);
    }

    @Override
    public ProjectSchedule getAllByProjectIdAndGroupCode(Long projectId, String groupCode) {
        return projectScheduleDaoI.getAllByProjectIdAndGroupCode(projectId, groupCode);
    }

    @Override
    public List<ProjectSchedule> getAllByProjectIdAndManagerIdOrderById(Long projectId, String managerId) {
        return projectScheduleDaoI.getAllByProjectIdAndManagerIdOrderById(projectId, managerId);
    }

    @Override
    public List<ProjectSchedule> getAllByQuery(Specification<ProjectSchedule> spec) {
        return projectScheduleDaoI.getAllByQuery(spec);
    }

    @Override
    public void deleteByGroupCode(String groupCode) {
        projectScheduleDaoI.deleteByGroupCode(groupCode);
    }

    @Override
    public void batchSave(List<ProjectSchedule> scheduleList) {
        projectScheduleDaoI.batchSave(scheduleList);
    }

    @Override
    public List<Map> getAllDistinctByProjectId(Long projectId) {
        List<Map> distinct = projectScheduleDaoI.getAllDistinctByProjectId(projectId);
        if (null == distinct) {
            distinct = new ArrayList<>();
        }
        return distinct;
    }

    @Override
    public void deleteAll(Long projectId) {
        projectScheduleDaoI.deleteAll(projectId);
    }

}
