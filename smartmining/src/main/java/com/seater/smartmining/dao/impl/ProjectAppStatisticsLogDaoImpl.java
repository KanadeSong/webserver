package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectAppStatisticsLogDaoI;
import com.seater.smartmining.entity.ProjectAppStatisticsLog;
import com.seater.smartmining.entity.repository.ProjectAppStatisticsLogRepository;
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
 * @Date 2019/4/11 0011 16:43
 */
@Component
public class ProjectAppStatisticsLogDaoImpl implements ProjectAppStatisticsLogDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectAppStatisticsLogRepository projectAppStatisticsLogRepository;
    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectappstatisticslog:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectAppStatisticsLog get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectAppStatisticsLog.class);
        }
        if(projectAppStatisticsLogRepository.existsById(id))
        {
            ProjectAppStatisticsLog log = projectAppStatisticsLogRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectAppStatisticsLog save(ProjectAppStatisticsLog log) throws JsonProcessingException {
        ProjectAppStatisticsLog log1 = projectAppStatisticsLogRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectAppStatisticsLogRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectAppStatisticsLog> query() {
        return projectAppStatisticsLogRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectAppStatisticsLog> query(Specification<ProjectAppStatisticsLog> spec) {
        return projectAppStatisticsLogRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectAppStatisticsLog> query(Pageable pageable) {
        return projectAppStatisticsLogRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectAppStatisticsLog> query(Specification<ProjectAppStatisticsLog> spec, Pageable pageable) {
        return projectAppStatisticsLogRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectAppStatisticsLog> getAll() {
        return projectAppStatisticsLogRepository.findAll();
    }

    @Override
    public void deleteByProjectAndReportDate(Long projectId, Date reportDate) {
        projectAppStatisticsLogRepository.deleteByProjectAndReportDate(projectId, reportDate);
    }

    @Override
    public List<ProjectAppStatisticsLog> getAllByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectAppStatisticsLogRepository.getAllByProjectIdAndReportDate(projectId, reportDate);
    }
}
