package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectSmartminingErrorLogDaoI;
import com.seater.smartmining.entity.ProjectSmartminingErrorLog;
import com.seater.smartmining.entity.repository.ProjectSmartminingErrorLogRepository;
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
 * @Date 2019/9/19 0019 15:35
 */
@Component
public class ProjectSmartminingErrorLogDaoImpl implements ProjectSmartminingErrorLogDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectSmartminingErrorLogRepository projectSmartminingErrorLogRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectsmartminingerrorlog:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectSmartminingErrorLog get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectSmartminingErrorLog.class);
        }
        if(projectSmartminingErrorLogRepository.existsById(id))
        {
            ProjectSmartminingErrorLog log = projectSmartminingErrorLogRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectSmartminingErrorLog save(ProjectSmartminingErrorLog log) throws IOException {
        ProjectSmartminingErrorLog log1 = projectSmartminingErrorLogRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectSmartminingErrorLogRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectSmartminingErrorLog> query() {
        return projectSmartminingErrorLogRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectSmartminingErrorLog> query(Specification<ProjectSmartminingErrorLog> spec) {
        return projectSmartminingErrorLogRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectSmartminingErrorLog> query(Pageable pageable) {
        return projectSmartminingErrorLogRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectSmartminingErrorLog> query(Specification<ProjectSmartminingErrorLog> spec, Pageable pageable) {
        return projectSmartminingErrorLogRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectSmartminingErrorLog> getAll() {
        return projectSmartminingErrorLogRepository.findAll();
    }
}
