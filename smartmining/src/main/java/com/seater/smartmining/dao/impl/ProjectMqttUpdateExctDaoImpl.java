package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectMqttUpdateExctDaoI;
import com.seater.smartmining.entity.ProjectMqttUpdateExct;
import com.seater.smartmining.entity.repository.ProjectMqttUpdateExctRepository;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/6 0006 17:31
 */
@Component
public class ProjectMqttUpdateExctDaoImpl implements ProjectMqttUpdateExctDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProjectMqttUpdateExctRepository projectMqttUpdateExctRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectmqttupdateExct:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectMqttUpdateExct get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectMqttUpdateExct.class);
        }
        if(projectMqttUpdateExctRepository.existsById(id)){
            ProjectMqttUpdateExct log = projectMqttUpdateExctRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectMqttUpdateExct save(ProjectMqttUpdateExct log) throws JsonProcessingException {
        ProjectMqttUpdateExct log1 = projectMqttUpdateExctRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectMqttUpdateExctRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectMqttUpdateExct> query() {
        return projectMqttUpdateExctRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectMqttUpdateExct> query(Specification<ProjectMqttUpdateExct> spec) {
        return projectMqttUpdateExctRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectMqttUpdateExct> query(Pageable pageable) {
        return projectMqttUpdateExctRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectMqttUpdateExct> query(Specification<ProjectMqttUpdateExct> spec, Pageable pageable) {
        return projectMqttUpdateExctRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectMqttUpdateExct> getAll() {
        return projectMqttUpdateExctRepository.findAll();
    }

    @Override
    public List<ProjectMqttUpdateExct> getAllByProjectIDAndSlagcarCodeAndCreateTime(Long projectId, String carCode, Date startTime, Date endTime) {
        return projectMqttUpdateExctRepository.getAllByProjectIDAndSlagcarCodeAndCreateTime(projectId, carCode, startTime, endTime);
    }
}
