package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.WorkMergeErrorLogDaoI;
import com.seater.smartmining.entity.WorkMergeErrorLog;
import com.seater.smartmining.entity.repository.ScheduleCarRepository;
import com.seater.smartmining.entity.repository.WorkMergeErrorLogRepository;
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
 * @Date 2019/7/4 0004 11:19
 */
@Component
public class WorkMergeErrorLogDaoImpl implements WorkMergeErrorLogDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    WorkMergeErrorLogRepository workMergeErrorLogRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:workmergeerrorlog:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public WorkMergeErrorLog get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, WorkMergeErrorLog.class);
        }
        if(workMergeErrorLogRepository.existsById(id)){
            WorkMergeErrorLog log = workMergeErrorLogRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public WorkMergeErrorLog save(WorkMergeErrorLog log) throws IOException {
        WorkMergeErrorLog log1 = workMergeErrorLogRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        workMergeErrorLogRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids){
            delete(id);
        }
    }

    @Override
    public Page<WorkMergeErrorLog> query() {
        return workMergeErrorLogRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<WorkMergeErrorLog> query(Specification<WorkMergeErrorLog> spec) {
        return workMergeErrorLogRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<WorkMergeErrorLog> query(Pageable pageable) {
        return workMergeErrorLogRepository.findAll(pageable);
    }

    @Override
    public Page<WorkMergeErrorLog> query(Specification<WorkMergeErrorLog> spec, Pageable pageable) {
        return workMergeErrorLogRepository.findAll(spec, pageable);
    }

    @Override
    public List<WorkMergeErrorLog> getAll() {
        return workMergeErrorLogRepository.findAll();
    }
}
