package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectErrorLoadLogDaoI;
import com.seater.smartmining.entity.ProjectErrorLoadLog;
import com.seater.smartmining.entity.repository.ProjectErrorLoadLogRepository;
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
 * @Date 2019/11/1 0001 0:21
 */
@Component
public class ProjectErrorLoadLogDaoImpl implements ProjectErrorLoadLogDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectErrorLoadLogRepository projectErrorLoadLogRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projecterrorloadlog:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectErrorLoadLog get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectErrorLoadLog.class);
        }
        if(projectErrorLoadLogRepository.existsById(id))
        {
            ProjectErrorLoadLog log = projectErrorLoadLogRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectErrorLoadLog save(ProjectErrorLoadLog log) throws IOException {
        ProjectErrorLoadLog log1 = projectErrorLoadLogRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectErrorLoadLogRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectErrorLoadLog> query() {
        return projectErrorLoadLogRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectErrorLoadLog> query(Specification<ProjectErrorLoadLog> spec) {
        return projectErrorLoadLogRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectErrorLoadLog> query(Pageable pageable) {
        return projectErrorLoadLogRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectErrorLoadLog> query(Specification<ProjectErrorLoadLog> spec, Pageable pageable) {
        return projectErrorLoadLogRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectErrorLoadLog> getAll() {
        return projectErrorLoadLogRepository.findAll();
    }

    @Override
    public ProjectErrorLoadLog getAllByProjectIdAndCarCodeAndDateIdentificationAndShift(Long projectId, String carCode, Date dateIdentification, Integer shift) {
        return projectErrorLoadLogRepository.getAllByProjectIdAndCarCodeAndDateIdentificationAndShift(projectId, carCode, dateIdentification, shift);
    }

    @Override
    public List<ProjectErrorLoadLog> getAllByProjectIdAndDateIdentificationAndShift(Long projectId, Date dateIdentification, Integer shift) {
        return projectErrorLoadLogRepository.getAllByProjectIdAndDateIdentificationAndShift(projectId, dateIdentification, shift);
    }
}
