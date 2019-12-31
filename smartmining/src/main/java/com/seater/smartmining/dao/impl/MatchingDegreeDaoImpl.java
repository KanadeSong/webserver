package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.MatchingDegreeDaoI;
import com.seater.smartmining.entity.MatchingDegree;
import com.seater.smartmining.entity.repository.MatchingDegreeRepository;
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

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/7/1 0001 11:49
 */
@Component
public class MatchingDegreeDaoImpl implements MatchingDegreeDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    MatchingDegreeRepository matchingDegreeRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:matchingdegree:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public MatchingDegree get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, MatchingDegree.class);
        }
        if(matchingDegreeRepository.existsById(id))
        {
            MatchingDegree log = matchingDegreeRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public MatchingDegree save(MatchingDegree log) throws IOException {
        MatchingDegree log1 = matchingDegreeRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        matchingDegreeRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<MatchingDegree> query() {
        return matchingDegreeRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<MatchingDegree> query(Specification<MatchingDegree> spec) {
        return matchingDegreeRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<MatchingDegree> query(Pageable pageable) {
        return matchingDegreeRepository.findAll(pageable);
    }

    @Override
    public Page<MatchingDegree> query(Specification<MatchingDegree> spec, Pageable pageable) {
        return matchingDegreeRepository.findAll(spec, pageable);
    }

    @Override
    public List<MatchingDegree> getAll() {
        return matchingDegreeRepository.findAll();
    }

    @Override
    public void deleteByProjectIdAndTimeAndType(Long projectId, Date date, Integer type, Integer shifts) {
        matchingDegreeRepository.deleteByProjectIdAndTimeAndType(projectId, date, type, shifts);
    }

    @Override
    public void batchSave(List<MatchingDegree> degreeList) {
        matchingDegreeRepository.saveAll(degreeList);
    }

    @Override
    public List<Map> getAllByProjectIdAndStartTimeAndEndTimeByWeek(Long projectId, Date startTime, Date endTime) {
        return matchingDegreeRepository.getAllByProjectIdAndStartTimeAndEndTimeByWeek(projectId, startTime, endTime);
    }
}
