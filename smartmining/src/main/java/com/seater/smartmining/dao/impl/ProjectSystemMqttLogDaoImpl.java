package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectSystemMqttLogDaoI;
import com.seater.smartmining.entity.ProjectSystemMqttLog;
import com.seater.smartmining.entity.repository.ProjectSystemMqttLogRepository;
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
 * @Date 2019/11/21 0021 12:50
 */
@Component
public class ProjectSystemMqttLogDaoImpl implements ProjectSystemMqttLogDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectSystemMqttLogRepository projectSystemMqttLogRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectsystemmqttlog:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectSystemMqttLog get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectSystemMqttLog.class);
        }
        if(projectSystemMqttLogRepository.existsById(id))
        {
            ProjectSystemMqttLog log = projectSystemMqttLogRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectSystemMqttLog save(ProjectSystemMqttLog log) throws IOException {
        ProjectSystemMqttLog log1 = projectSystemMqttLogRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectSystemMqttLogRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectSystemMqttLog> query() {
        return projectSystemMqttLogRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectSystemMqttLog> query(Specification<ProjectSystemMqttLog> spec) {
        return projectSystemMqttLogRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectSystemMqttLog> query(Pageable pageable) {
        return projectSystemMqttLogRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectSystemMqttLog> query(Specification<ProjectSystemMqttLog> spec, Pageable pageable) {
        return projectSystemMqttLogRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectSystemMqttLog> getAll() {
        return projectSystemMqttLogRepository.findAll();
    }
}
