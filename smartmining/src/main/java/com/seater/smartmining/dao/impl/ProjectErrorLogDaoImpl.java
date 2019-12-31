package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectErrorLogDaoI;
import com.seater.smartmining.entity.ProjectErrorLog;
import com.seater.smartmining.entity.repository.ProjectErrorLogRepository;
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
 * @Date 2019/5/22 0022 17:23
 */
@Component
public class ProjectErrorLogDaoImpl implements ProjectErrorLogDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectErrorLogRepository projectErrorLogRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projecterrorlog:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectErrorLog get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectErrorLog.class);
        }
        if(projectErrorLogRepository.existsById(id))
        {
            ProjectErrorLog log = projectErrorLogRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectErrorLog save(ProjectErrorLog log) throws IOException {
        ProjectErrorLog log1 = projectErrorLogRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectErrorLogRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectErrorLog> query() {
        return projectErrorLogRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectErrorLog> query(Specification<ProjectErrorLog> spec) {
        return projectErrorLogRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectErrorLog> query(Pageable pageable) {
        return projectErrorLogRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectErrorLog> query(Specification<ProjectErrorLog> spec, Pageable pageable) {
        return projectErrorLogRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectErrorLog> getAll() {
        return projectErrorLogRepository.findAll();
    }
}
