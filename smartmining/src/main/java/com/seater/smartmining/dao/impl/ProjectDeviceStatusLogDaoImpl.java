package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectDeviceStatusLogDaoI;
import com.seater.smartmining.entity.ProjectDeviceStatusLog;
import com.seater.smartmining.entity.repository.ProjectDeviceStatusLogRepository;
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
 * @Date 2019/11/21 0021 15:10
 */
@Component
public class ProjectDeviceStatusLogDaoImpl implements ProjectDeviceStatusLogDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectDeviceStatusLogRepository projectDeviceStatusLogRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectdevicestatuslog:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectDeviceStatusLog get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectDeviceStatusLog.class);
        }
        if(projectDeviceStatusLogRepository.existsById(id))
        {
            ProjectDeviceStatusLog log = projectDeviceStatusLogRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectDeviceStatusLog save(ProjectDeviceStatusLog log) throws IOException {
        ProjectDeviceStatusLog log1 = projectDeviceStatusLogRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectDeviceStatusLogRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectDeviceStatusLog> query() {
        return projectDeviceStatusLogRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDeviceStatusLog> query(Specification<ProjectDeviceStatusLog> spec) {
        return projectDeviceStatusLogRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDeviceStatusLog> query(Pageable pageable) {
        return projectDeviceStatusLogRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectDeviceStatusLog> query(Specification<ProjectDeviceStatusLog> spec, Pageable pageable) {
        return projectDeviceStatusLogRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectDeviceStatusLog> getAll() {
        return projectDeviceStatusLogRepository.findAll();
    }

    @Override
    public ProjectDeviceStatusLog getAllByUid(String uid) {
        return projectDeviceStatusLogRepository.getAllByUid(uid);
    }

    @Override
    public List<ProjectDeviceStatusLog> getAllByUnlineTime() {
        return projectDeviceStatusLogRepository.getAllByUnlineTime();
    }
}
