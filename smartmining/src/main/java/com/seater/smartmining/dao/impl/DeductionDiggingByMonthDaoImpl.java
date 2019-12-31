package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.DeductionDiggingByMonthDaoI;
import com.seater.smartmining.entity.DeductionDiggingByMonth;
import com.seater.smartmining.entity.repository.DeductionDiggingByMonthRepository;
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
 * @Date 2019/5/10 0010 13:44
 */
@Component
public class DeductionDiggingByMonthDaoImpl implements DeductionDiggingByMonthDaoI {

    @Autowired
    DeductionDiggingByMonthRepository deductionDiggingByMonthRepository;

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:deductiondiggingbymonth:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public DeductionDiggingByMonth get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, DeductionDiggingByMonth.class);
        }
        if(deductionDiggingByMonthRepository.existsById(id))
        {
            DeductionDiggingByMonth log = deductionDiggingByMonthRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public DeductionDiggingByMonth save(DeductionDiggingByMonth log) throws IOException {
        DeductionDiggingByMonth log1 = deductionDiggingByMonthRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        deductionDiggingByMonthRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<DeductionDiggingByMonth> query() {
        return deductionDiggingByMonthRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<DeductionDiggingByMonth> query(Specification<DeductionDiggingByMonth> spec) {
        return deductionDiggingByMonthRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<DeductionDiggingByMonth> query(Pageable pageable) {
        return deductionDiggingByMonthRepository.findAll(pageable);
    }

    @Override
    public Page<DeductionDiggingByMonth> query(Specification<DeductionDiggingByMonth> spec, Pageable pageable) {
        return deductionDiggingByMonthRepository.findAll(spec,pageable);
    }

    @Override
    public List<DeductionDiggingByMonth> getAll() {
        return deductionDiggingByMonthRepository.findAll();
    }

    @Override
    public List<DeductionDiggingByMonth> getAllByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return deductionDiggingByMonthRepository.getAllByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public List<DeductionDiggingByMonth> saveAll(List<DeductionDiggingByMonth> saveList) {
        return deductionDiggingByMonthRepository.saveAll(saveList);
    }
}
