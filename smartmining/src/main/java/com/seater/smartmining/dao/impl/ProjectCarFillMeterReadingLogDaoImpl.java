package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectCarFillMeterReadingLogDaoI;
import com.seater.smartmining.entity.ProjectCarFillMeterReadingLog;
import com.seater.smartmining.entity.repository.ProjectCarFillMeterReadingLogRepository;
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
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class ProjectCarFillMeterReadingLogDaoImpl implements ProjectCarFillMeterReadingLogDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectCarFillMeterReadingLogRepository projectCarFillMeterReadingLogRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectCarFillMeterReadingLog:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}


    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectCarFillMeterReadingLog> query(Specification< ProjectCarFillMeterReadingLog> spec, Pageable pageable) {
        return projectCarFillMeterReadingLogRepository.findAll(spec, pageable);
    }

    @Override
    public Page< ProjectCarFillMeterReadingLog> query(Specification< ProjectCarFillMeterReadingLog> spec) {
        return projectCarFillMeterReadingLogRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectCarFillMeterReadingLog> query(Pageable pageable) {
        return projectCarFillMeterReadingLogRepository.findAll(pageable);
    }

    @Override
    public Page< ProjectCarFillMeterReadingLog> query() {
        return projectCarFillMeterReadingLogRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public  ProjectCarFillMeterReadingLog get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj,  ProjectCarFillMeterReadingLog.class);
        }
        if(projectCarFillMeterReadingLogRepository.existsById(id))
        {
            ProjectCarFillMeterReadingLog log = projectCarFillMeterReadingLogRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public  ProjectCarFillMeterReadingLog save( ProjectCarFillMeterReadingLog log) throws IOException {
        ProjectCarFillMeterReadingLog log1 = projectCarFillMeterReadingLogRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectCarFillMeterReadingLogRepository.deleteById(id);
    }

    @Override
    public List<ProjectCarFillMeterReadingLog> getAll() {
        return projectCarFillMeterReadingLogRepository.findAll();
    }

    @Override
    public ProjectCarFillMeterReadingLog getByProjectIdAndOilCarIdAndAddTime(Long projectId,Long oilCarId, Date addTime) {
        return projectCarFillMeterReadingLogRepository.getByProjectIdAndOilCarIdAndAddTime(projectId,oilCarId,addTime);
    }

    @Override
    public List<ProjectCarFillMeterReadingLog> queryWx(Specification<ProjectCarFillMeterReadingLog> spec) {
        return projectCarFillMeterReadingLogRepository.findAll(spec);
    }

    @Override
    public ProjectCarFillMeterReadingLog querySingle(Specification<ProjectCarFillMeterReadingLog> spec) {
        return projectCarFillMeterReadingLogRepository.findOne(spec).get();
    }

    @Override
    public Long getHistoryByOilCarId(Long oilCarId) {
        return projectCarFillMeterReadingLogRepository.getHistoryByOilCarId(oilCarId);
    }

}
