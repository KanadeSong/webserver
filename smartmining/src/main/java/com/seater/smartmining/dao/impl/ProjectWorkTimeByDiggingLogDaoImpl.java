package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectWorkTimeByDiggingLogDaoI;
import com.seater.smartmining.entity.ProjectWorkTimeByDiggingLog;
import com.seater.smartmining.entity.repository.ProjectWorkTimeByDiggingLogRepository;
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
 * @Date 2019/11/23 0023 15:57
 */
@Component
public class ProjectWorkTimeByDiggingLogDaoImpl implements ProjectWorkTimeByDiggingLogDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectWorkTimeByDiggingLogRepository projectWorkTimeByDiggingLogRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectworktimebydigginglog:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectWorkTimeByDiggingLog get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj,  ProjectWorkTimeByDiggingLog.class);
        }
        if(projectWorkTimeByDiggingLogRepository.existsById(id))
        {
            ProjectWorkTimeByDiggingLog log = projectWorkTimeByDiggingLogRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectWorkTimeByDiggingLog save(ProjectWorkTimeByDiggingLog log) throws IOException {
        ProjectWorkTimeByDiggingLog log1 = projectWorkTimeByDiggingLogRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectWorkTimeByDiggingLogRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectWorkTimeByDiggingLog> query() {
        return projectWorkTimeByDiggingLogRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectWorkTimeByDiggingLog> query(Specification<ProjectWorkTimeByDiggingLog> spec) {
        return projectWorkTimeByDiggingLogRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectWorkTimeByDiggingLog> query(Pageable pageable) {
        return projectWorkTimeByDiggingLogRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectWorkTimeByDiggingLog> query(Specification<ProjectWorkTimeByDiggingLog> spec, Pageable pageable) {
        return projectWorkTimeByDiggingLogRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectWorkTimeByDiggingLog> getAll() {
        return projectWorkTimeByDiggingLogRepository.findAll();
    }

    @Override
    public List<ProjectWorkTimeByDiggingLog> getAllByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift) {
        return projectWorkTimeByDiggingLogRepository.getAllByProjectIdAndDateIdentificationAndShift(projectId, date, shift);
    }
}
