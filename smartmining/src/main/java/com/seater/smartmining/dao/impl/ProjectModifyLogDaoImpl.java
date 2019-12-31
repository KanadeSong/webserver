package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectModifyLogDaoI;
import com.seater.smartmining.entity.ProjectModifyLog;
import com.seater.smartmining.entity.repository.ProjectModifyLogRepository;
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
 * @Date 2019/7/16 0016 11:02
 */
@Component
public class ProjectModifyLogDaoImpl implements ProjectModifyLogDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectModifyLogRepository projectModifyLogRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectmodifylog:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectModifyLog get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectModifyLog.class);
        }
        if(projectModifyLogRepository.existsById(id))
        {
            ProjectModifyLog log = projectModifyLogRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectModifyLog save(ProjectModifyLog log) throws IOException {
        ProjectModifyLog log1 = projectModifyLogRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectModifyLogRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectModifyLog> query() {
        return projectModifyLogRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectModifyLog> query(Specification<ProjectModifyLog> spec) {
        return projectModifyLogRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectModifyLog> query(Pageable pageable) {
        return projectModifyLogRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectModifyLog> query(Specification<ProjectModifyLog> spec, Pageable pageable) {
        return projectModifyLogRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectModifyLog> getAll() {
        return projectModifyLogRepository.findAll();
    }

    @Override
    public void batchSave(List<ProjectModifyLog> logList) {
        projectModifyLogRepository.saveAll(logList);
    }
}
