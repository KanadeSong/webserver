package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.InterPhoneScheduleDaoI;
import com.seater.smartmining.entity.InterPhoneSchedule;
import com.seater.smartmining.entity.repository.InterPhoneScheduleRepository;
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
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/9/19 14:21
 */
@Component
public class InterPhoneScheduleDaoImpl implements InterPhoneScheduleDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    InterPhoneScheduleRepository interPhoneScheduleRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:interPhoneSchedule:";

    String getKey(Long id) {
        return keyGroup + id.toString();
    }

    ValueOperations<String, String> getValueOps() {
        if (valueOps == null) valueOps = stringRedisTemplate.opsForValue();
        return valueOps;
    }


    @Override
    public void delete(List<Long> ids) {
        for (Long id : ids) {
            delete(id);
        }
    }

    @Override
    public Page<InterPhoneSchedule> query(Specification<InterPhoneSchedule> spec, Pageable pageable) {
        return interPhoneScheduleRepository.findAll(spec, pageable);
    }

    @Override
    public Page<InterPhoneSchedule> query(Specification<InterPhoneSchedule> spec) {
        return interPhoneScheduleRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<InterPhoneSchedule> query(Pageable pageable) {
        return interPhoneScheduleRepository.findAll(pageable);
    }

    @Override
    public Page<InterPhoneSchedule> query() {
        return interPhoneScheduleRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public InterPhoneSchedule get(Long id) throws IOException {
        if (id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if (obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, InterPhoneSchedule.class);
        }
        if (interPhoneScheduleRepository.existsById(id)) {
            InterPhoneSchedule log = interPhoneScheduleRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public InterPhoneSchedule save(InterPhoneSchedule log) throws IOException {
        InterPhoneSchedule log1 = interPhoneScheduleRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if (id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        interPhoneScheduleRepository.deleteById(id);
    }

    @Override
    public List<InterPhoneSchedule> getAll() {
        return interPhoneScheduleRepository.findAll();
    }

    @Override
    public List<InterPhoneSchedule> queryWx(Specification<InterPhoneSchedule> spec) {
        return interPhoneScheduleRepository.findAll(spec);
    }

    @Override
    public void deleteAllByScheduleId(Long scheduleId) {
        interPhoneScheduleRepository.deleteAllByScheduleId(scheduleId);
    }

    @Override
    public void deleteAllByProjectId(Long projectId) {
        interPhoneScheduleRepository.deleteAllByProjectId(projectId);
    }

    @Override
    public void deleteAllByInterPhoneGroupId(Long interPhoneGroupId) {
        interPhoneScheduleRepository.deleteAllByInterPhoneGroupId(interPhoneGroupId);
    }

}
