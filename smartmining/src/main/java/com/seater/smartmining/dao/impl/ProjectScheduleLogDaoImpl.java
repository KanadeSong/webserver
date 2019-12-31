package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectScheduleLogDaoI;
import com.seater.smartmining.entity.ProjectScheduleLog;
import com.seater.smartmining.entity.repository.ProjectScheduleLogRepository;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/7/23 0023 10:07
 */
@Component
public class ProjectScheduleLogDaoImpl implements ProjectScheduleLogDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectScheduleLogRepository projectScheduleLogRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectlogschedule:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectScheduleLog get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectScheduleLog.class);
        }
        if(projectScheduleLogRepository.existsById(id)){
            ProjectScheduleLog log = projectScheduleLogRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectScheduleLog save(ProjectScheduleLog log) throws JsonProcessingException {
        ProjectScheduleLog log1 = projectScheduleLogRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectScheduleLogRepository.deleteById(id);
    }

    @Override
    public Page<ProjectScheduleLog> query() {
        return projectScheduleLogRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectScheduleLog> query(Specification<ProjectScheduleLog> spec) {
        return projectScheduleLogRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectScheduleLog> query(Pageable pageable) {
        return projectScheduleLogRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectScheduleLog> query(Specification<ProjectScheduleLog> spec, Pageable pageable) {
        return projectScheduleLogRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectScheduleLog> getAll() {
        return projectScheduleLogRepository.findAll();
    }
}
