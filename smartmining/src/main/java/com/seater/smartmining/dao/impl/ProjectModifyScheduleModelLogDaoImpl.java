package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectModifyScheduleModelLogDaoI;
import com.seater.smartmining.entity.ProjectModifyScheduleModelLog;
import com.seater.smartmining.entity.repository.ProjectModifyScheduleModelLogRepository;
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
 * @Date 2019/11/15 0015 14:14
 */
@Component
public class ProjectModifyScheduleModelLogDaoImpl implements ProjectModifyScheduleModelLogDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProjectModifyScheduleModelLogRepository projectModifyScheduleModelLogRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectmodifyschedulemodellog:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectModifyScheduleModelLog get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null){
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectModifyScheduleModelLog.class);
        }
        if(projectModifyScheduleModelLogRepository.existsById(id)){
            ProjectModifyScheduleModelLog log = projectModifyScheduleModelLogRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectModifyScheduleModelLog save(ProjectModifyScheduleModelLog log) throws IOException {
        ProjectModifyScheduleModelLog log1 = projectModifyScheduleModelLogRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectModifyScheduleModelLogRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectModifyScheduleModelLog> query() {
        return projectModifyScheduleModelLogRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectModifyScheduleModelLog> query(Specification<ProjectModifyScheduleModelLog> spec) {
        return projectModifyScheduleModelLogRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectModifyScheduleModelLog> query(Pageable pageable) {
        return projectModifyScheduleModelLogRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectModifyScheduleModelLog> query(Specification<ProjectModifyScheduleModelLog> spec, Pageable pageable) {
        return projectModifyScheduleModelLogRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectModifyScheduleModelLog> getAll() {
        return projectModifyScheduleModelLogRepository.findAll();
    }
}
