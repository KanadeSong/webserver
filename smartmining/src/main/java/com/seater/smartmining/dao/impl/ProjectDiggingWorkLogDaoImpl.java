package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectDiggingWorkLogDaoI;
import com.seater.smartmining.entity.ProjectDiggingWorkLog;
import com.seater.smartmining.entity.repository.ProjectDiggingWorkLogRepository;
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
 * @Date 2019/9/18 0018 17:53
 */
@Component
public class ProjectDiggingWorkLogDaoImpl implements ProjectDiggingWorkLogDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProjectDiggingWorkLogRepository projectDiggingPartCountRepository;
    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectdiggingworklog:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}


    @Override
    public ProjectDiggingWorkLog get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectDiggingWorkLog.class);
        }
        if(projectDiggingPartCountRepository.existsById(id))
        {
            ProjectDiggingWorkLog log = projectDiggingPartCountRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectDiggingWorkLog save(ProjectDiggingWorkLog log) throws IOException {
        ProjectDiggingWorkLog log1 = projectDiggingPartCountRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectDiggingPartCountRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectDiggingWorkLog> query() {
        return projectDiggingPartCountRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDiggingWorkLog> query(Specification<ProjectDiggingWorkLog> spec) {
        return projectDiggingPartCountRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDiggingWorkLog> query(Pageable pageable) {
        return projectDiggingPartCountRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectDiggingWorkLog> query(Specification<ProjectDiggingWorkLog> spec, Pageable pageable) {
        return projectDiggingPartCountRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectDiggingWorkLog> getAll() {
        return projectDiggingPartCountRepository.findAll();
    }
}
