package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectCarCountLogDaoI;
import com.seater.smartmining.entity.ProjectCarCountLog;
import com.seater.smartmining.entity.repository.ProjectCarCountLogRepository;
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
 * @Date 2019/12/2 0002 14:44
 */
@Component
public class ProjectCarCountLogDaoImpl implements ProjectCarCountLogDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectCarCountLogRepository projectCarCountLogRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectcarcountlog:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectCarCountLog get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectCarCountLog.class);
        }
        if(projectCarCountLogRepository.existsById(id))
        {
            ProjectCarCountLog log = projectCarCountLogRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectCarCountLog save(ProjectCarCountLog log) throws IOException {
        ProjectCarCountLog log1 = projectCarCountLogRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectCarCountLogRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectCarCountLog> query() {
        return projectCarCountLogRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectCarCountLog> query(Specification<ProjectCarCountLog> spec) {
        return projectCarCountLogRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectCarCountLog> query(Pageable pageable) {
        return projectCarCountLogRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectCarCountLog> query(Specification<ProjectCarCountLog> spec, Pageable pageable) {
        return projectCarCountLogRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectCarCountLog> getAll() {
        return projectCarCountLogRepository.findAll();
    }
}
