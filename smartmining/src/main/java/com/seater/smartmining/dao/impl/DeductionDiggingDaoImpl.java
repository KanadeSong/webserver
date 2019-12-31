package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.DeductionDiggingDaoI;
import com.seater.smartmining.entity.DeductionDigging;
import com.seater.smartmining.entity.repository.DeductionDiggingRepository;
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
 * @Date 2019/5/8 0008 0:47
 */
@Component
public class DeductionDiggingDaoImpl implements DeductionDiggingDaoI {

    @Autowired
    DeductionDiggingRepository deductionDiggingRepository;

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:deductiondigging:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}


    @Override
    public DeductionDigging get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, DeductionDigging.class);
        }
        if(deductionDiggingRepository.existsById(id))
        {
            DeductionDigging log = deductionDiggingRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public DeductionDigging save(DeductionDigging log) throws IOException {
        DeductionDigging log1 = deductionDiggingRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        deductionDiggingRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<DeductionDigging> query() {
        return deductionDiggingRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<DeductionDigging> query(Specification<DeductionDigging> spec) {
        return deductionDiggingRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<DeductionDigging> query(Pageable pageable) {
        return deductionDiggingRepository.findAll(pageable);
    }

    @Override
    public Page<DeductionDigging> query(Specification<DeductionDigging> spec, Pageable pageable) {
        return deductionDiggingRepository.findAll(spec,pageable);
    }

    @Override
    public List<DeductionDigging> getAll() {
        return deductionDiggingRepository.findAll();
    }

    @Override
    public DeductionDigging getAllByProjectIdAndMachineIdAndReportDate(Long projectId, Long machineId, Date reportDate) {
        return deductionDiggingRepository.getAllByProjectIdAndMachineIdAndReportDate(projectId, machineId, reportDate);
    }

    @Override
    public List<DeductionDigging> getAllByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return deductionDiggingRepository.getAllByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public List<DeductionDigging> getAllByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return deductionDiggingRepository.getAllByProjectIdAndTime(projectId, startTime, endTime);
    }
}
