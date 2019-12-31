package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectCheckLogDaoI;
import com.seater.smartmining.entity.ProjectCheckLog;
import com.seater.smartmining.entity.repository.ProjectCheckLogRepository;
import com.seater.user.dao.GlobalSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class ProjectCheckLogDaoImpl implements ProjectCheckLogDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectCheckLogRepository projectCheckLogRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectchecklog:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}


    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectCheckLog> query(Specification<ProjectCheckLog> spec, Pageable pageable) {
        return projectCheckLogRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ProjectCheckLog> query(Specification<ProjectCheckLog> spec) {
        return projectCheckLogRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectCheckLog> query(Pageable pageable) {
        return projectCheckLogRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectCheckLog> query() {
        return projectCheckLogRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public ProjectCheckLog get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectCheckLog.class);
        }
        if(projectCheckLogRepository.existsById(id))
        {
            ProjectCheckLog log = projectCheckLogRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectCheckLog save(ProjectCheckLog log) throws IOException {
        ProjectCheckLog log1 = projectCheckLogRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectCheckLogRepository.deleteById(id);
    }

    @Override
    public List<ProjectCheckLog> getAll() {
        return projectCheckLogRepository.findAll();
    }

    @Override
    public List<Map> getCheckCountByProjectIDAndTimeCheck(Long projectId, Date startTime, Date endTime) {
        return projectCheckLogRepository.getCheckCountByProjectIDAndTimeCheck(projectId, startTime, endTime);
    }
}
