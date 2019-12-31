package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectScheduleDaoI;
import com.seater.smartmining.entity.ProjectSchedule;
import com.seater.smartmining.entity.repository.ProjectScheduleRepository;
import com.seater.user.dao.GlobalSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/23 0023 14:18
 */
@Component
public class ProjectScheduleDaoImpl implements ProjectScheduleDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectScheduleRepository projectScheduleRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectschedule:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectSchedule get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectSchedule.class);
        }
        if(projectScheduleRepository.existsById(id)){
            ProjectSchedule log = projectScheduleRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectSchedule save(ProjectSchedule log) throws JsonProcessingException {
        ProjectSchedule log1 = projectScheduleRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectScheduleRepository.deleteById(id);
    }

    @Override
    public Page<ProjectSchedule> query() {
        return projectScheduleRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectSchedule> query(Specification<ProjectSchedule> spec) {
        return projectScheduleRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectSchedule> query(Pageable pageable) {
        return projectScheduleRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectSchedule> query(Specification<ProjectSchedule> spec, Pageable pageable) {
        return projectScheduleRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectSchedule> getAll() {
        return projectScheduleRepository.findAll();
    }

    @Override
    public void deleteByProjectIdAndGroupCode(Long projectId, String groupCode) {
        projectScheduleRepository.deleteByProjectIdAndGroupCode(projectId, groupCode);
    }

    @Override
    public List<ProjectSchedule> getAllByProjectId(Long projectId) {
        return projectScheduleRepository.getAllByProjectId(projectId);
    }

    @Override
    public List<ProjectSchedule> getAllByProjectIdAndManagerId(Long projectId, String managerId, Integer current, Integer pageSize) {
        return projectScheduleRepository.getAllByProjectIdAndManagerId(projectId, managerId, current, pageSize);
    }

    @Override
    public ProjectSchedule getAllByProjectIdAndGroupCode(Long projectId, String groupCode) {
        return projectScheduleRepository.getAllByProjectIdAndGroupCode(projectId, groupCode);
    }

    @Override
    public List<ProjectSchedule> getAllByProjectIdAndManagerIdOrderById(Long projectId, String managerId) {
        return projectScheduleRepository.getAllByProjectIdAndManagerIdOrderById(projectId, managerId);
    }

    @Override
    public List<ProjectSchedule> getAllByQuery(Specification<ProjectSchedule> spec) {
        return projectScheduleRepository.findAll(spec);
    }

    @Override
    public void deleteByGroupCode(String groupCode) {
        projectScheduleRepository.deleteByGroupCode(groupCode);
    }

    @Override
    public void batchSave(List<ProjectSchedule> scheduleList) {
        for (ProjectSchedule schedule : scheduleList) {
            getValueOps().getOperations().delete(getKey(schedule.getId()));
        }
        projectScheduleRepository.saveAll(scheduleList);
    }

    @Override
    public void deleteAll(Long projectId) {
        projectScheduleRepository.deleteAll(projectId);
    }

    @Override
    public List<Map> getAllDistinctByProjectId(Long projectId) {
        return projectScheduleRepository.getAllDistinctByProjectId(projectId);
    }
}
