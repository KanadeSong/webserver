package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectTempSiteLogDaoI;
import com.seater.smartmining.entity.ProjectTempSiteLog;
import com.seater.smartmining.entity.repository.ProjectTempSiteLogRepository;
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
 * @Date 2019/9/21 0021 11:32
 */
@Component
public class ProjectTempSiteLogDaoImpl implements ProjectTempSiteLogDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectTempSiteLogRepository projectTempSiteLogRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projecttempsitelog:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectTempSiteLog get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj,  ProjectTempSiteLog.class);
        }
        if(projectTempSiteLogRepository.existsById(id))
        {
            ProjectTempSiteLog log = projectTempSiteLogRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectTempSiteLog save(ProjectTempSiteLog log) throws IOException {
        ProjectTempSiteLog log1 = projectTempSiteLogRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectTempSiteLogRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectTempSiteLog> query() {
        return projectTempSiteLogRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectTempSiteLog> query(Specification<ProjectTempSiteLog> spec) {
        return projectTempSiteLogRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectTempSiteLog> query(Pageable pageable) {
        return projectTempSiteLogRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectTempSiteLog> query(Specification<ProjectTempSiteLog> spec, Pageable pageable) {
        return projectTempSiteLogRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectTempSiteLog> queryAll(Specification<ProjectTempSiteLog> specification) {
        return projectTempSiteLogRepository.findAll(specification);
    }

    @Override
    public List<ProjectTempSiteLog> getAll() {
        return projectTempSiteLogRepository.findAll();
    }

    @Override
    public Date getMaxUnloadDateByCarCode(String carCode) {
        List<Date> list = projectTempSiteLogRepository.getMaxUnloadDateByCarCode(carCode);
        if(list == null || list.size() <= 0)
            return null;

        return list.get(0);
    }
}
