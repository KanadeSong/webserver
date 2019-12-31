package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectDeviceElectrifyLogDaoI;
import com.seater.smartmining.entity.ProjectDeviceElectrifyLog;
import com.seater.smartmining.entity.repository.ProjectDeviceElectrifyLogRepository;
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
 * @Date 2019/12/10 0010 16:38
 */
@Component
public class ProjectDeviceElectrifyLogDaoImpl implements ProjectDeviceElectrifyLogDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectDeviceElectrifyLogRepository projectDeviceElectrifyLogRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectdeviceElectrifylog:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectDeviceElectrifyLog get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectDeviceElectrifyLog.class);
        }
        if(projectDeviceElectrifyLogRepository.existsById(id))
        {
            ProjectDeviceElectrifyLog log = projectDeviceElectrifyLogRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectDeviceElectrifyLog save(ProjectDeviceElectrifyLog log) throws IOException {
        ProjectDeviceElectrifyLog log1 = projectDeviceElectrifyLogRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectDeviceElectrifyLogRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectDeviceElectrifyLog> query() {
        return projectDeviceElectrifyLogRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDeviceElectrifyLog> query(Specification<ProjectDeviceElectrifyLog> spec) {
        return projectDeviceElectrifyLogRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDeviceElectrifyLog> query(Pageable pageable) {
        return projectDeviceElectrifyLogRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectDeviceElectrifyLog> query(Specification<ProjectDeviceElectrifyLog> spec, Pageable pageable) {
        return projectDeviceElectrifyLogRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectDeviceElectrifyLog> getAll() {
        return projectDeviceElectrifyLogRepository.findAll();
    }

    @Override
    public ProjectDeviceElectrifyLog getAllByProjectIdAndUidElectrifyTime(Long projectId, String carCode, Date date, Integer deviceType) {
        return projectDeviceElectrifyLogRepository.getAllByProjectIdAndUidElectrifyTime(projectId, carCode, date, deviceType);
    }
}
